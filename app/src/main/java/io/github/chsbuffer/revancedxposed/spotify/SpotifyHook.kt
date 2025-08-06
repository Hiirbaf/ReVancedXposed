package io.github.chsbuffer.revancedxposed.spotify

import android.app.Application
import app.revanced.extension.shared.Logger
import app.revanced.extension.shared.Utils
import app.revanced.extension.spotify.misc.UnlockPremiumPatch
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import io.github.chsbuffer.revancedxposed.BaseHook
import io.github.chsbuffer.revancedxposed.Opcode
import io.github.chsbuffer.revancedxposed.callMethod
import io.github.chsbuffer.revancedxposed.findField
import io.github.chsbuffer.revancedxposed.findFirstFieldByExactType
import io.github.chsbuffer.revancedxposed.fingerprint
import io.github.chsbuffer.revancedxposed.injectHostClassLoaderToSelf
import io.github.chsbuffer.revancedxposed.strings
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType
import org.luckypray.dexkit.query.enums.UsingType
import org.luckypray.dexkit.result.MethodData
import org.luckypray.dexkit.wrap.DexField
import org.luckypray.dexkit.wrap.DexMethod
import java.lang.reflect.Field

@Suppress("UNCHECKED_CAST")
class SpotifyHook(app: Application, lpparam: LoadPackageParam) : BaseHook(app, lpparam) {
    override val hooks = arrayOf(
        ::Extension,
        ::SanitizeSharingLinks,
        ::UnlockPremium,
        ::FixThirdPartyLaunchersWidgets
    )

    fun Extension() {
        // Logger
        Utils.setContext(app)

        // load stubbed spotify classes
        injectHostClassLoaderToSelf(this::class.java.classLoader!!, classLoader)
    }
}

@Suppress("UNCHECKED_CAST")
fun SpotifyHook.UnlockPremium() {
    // Override the attributes map in the getter method.
    getDexMethod("productStateProtoFingerprint") {
        fingerprint {
            returns("Ljava/util/Map;")
            classMatcher { descriptor = "Lcom/spotify/remoteconfig/internal/ProductStateProto;" }
        }.also { method ->
            getDexField("attributesMapField") {
                method.usingFields.single().field
            }
        }
    }.hookMethod(object : XC_MethodHook() {
        val field = getDexField("attributesMapField").toField()
        override fun beforeHookedMethod(param: MethodHookParam) {
            Logger.printDebug { field.get(param.thisObject)!!.toString() }
            UnlockPremiumPatch.overrideAttributes(field.get(param.thisObject) as Map<String, *>)
        }
    })

    // Add the query parameter trackRows to show popular tracks in the artist page.
    getDexMethod("buildQueryParametersFingerprint") {
        findMethod {
            matcher {
                strings("trackRows", "device_type:tablet")
            }
        }.single()
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val result = param.result
            val FIELD = "checkDeviceCapability"
            if (result.toString().contains("${FIELD}=")) {
                param.result = XposedBridge.invokeOriginalMethod(
                    param.method, param.thisObject, arrayOf(param.args[0], true)
                )
            }
        }
    })

    // Enable choosing a specific song/artist via Google Assistant.
    getDexMethod("contextFromJsonFingerprint") {
        fingerprint {
            opcodes(
                Opcode.INVOKE_STATIC,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.INVOKE_VIRTUAL,
                Opcode.MOVE_RESULT_OBJECT,
                Opcode.INVOKE_STATIC
            )
            methodMatcher {
                name("fromJson")
                declaredClass(
                    "voiceassistants.playermodels.ContextJsonAdapter", StringMatchType.EndsWith
                )
            }
        }
    }.hookMethod(object : XC_MethodHook() {
        fun removeStationString(field: Field, obj: Any) {
            field.set(obj, UnlockPremiumPatch.removeStationString(field.get(obj) as String))
        }

        override fun afterHookedMethod(param: MethodHookParam) {
            val thiz = param.result
            val clazz = param.result.javaClass
            removeStationString(clazz.findField("uri"), thiz)
            removeStationString(clazz.findField("url"), thiz)
        }
    })

    // Disable forced shuffle when asking for an album/playlist via Google Assistant.
    XposedHelpers.findAndHookMethod(
        "com.spotify.player.model.command.options.AutoValue_PlayerOptionOverrides\$Builder",
        classLoader,
        "build",
        object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                param.thisObject.callMethod("shufflingContext", false)
            }
        })

    // Hook the method which adds context menu items and return before adding if the item is a Premium ad.
    val contextMenuViewModelClass = getDexClass("contextMenuViewModelClassFingerprint") {
        fingerprint {
            strings("ContextMenuViewModel(header=")
        }.declaredClass!!
    }

    runCatching {
        getDexMethod("oldContextMenuViewModelAddItemFingerprint") {
            fingerprint {
                classMatcher { className(contextMenuViewModelClass.className) }
                parameters("L")
                returns("V")
                methodMatcher { addInvoke { name = "add" } }
            }
        }.hookMethod(object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (UnlockPremiumPatch.isFilteredContextMenuItem(param.args[0].callMethod("getViewModel"))) {
                    param.result = null
                }
            }
        })
        Logger.printDebug { "Patch used in versions older than \"9.0.60.128\"." }
    }.onFailure {
        Logger.printDebug { "Patch for newest versions. $it" }
        XposedBridge.hookAllConstructors(
            contextMenuViewModelClass.toClass(), object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    val parameterTypes =
                        (param.method as java.lang.reflect.Constructor<*>).parameterTypes
                    Logger.printDebug { "ContextMenuViewModel(${parameterTypes.joinToString(",") { it.name }})" }
                    for (i in 0 until param.args.size) {
                        if (parameterTypes[i].name != "java.util.List") continue
                        val original = param.args[i] as? List<*> ?: continue
                        val filtered = UnlockPremiumPatch.filterContextMenuItems(original)
                        param.args[i] = filtered
                        Logger.printDebug { "Filtered ${original.size - filtered.size} context menu items." }
                    }
                }
            })
    }

    fun DexKitBridge.structureGetSectionsFingerprint(className: String): MethodData {
        return fingerprint {
            classMatcher { className(className, StringMatchType.EndsWith) }
            methodMatcher {
                addUsingField {
                    usingType = UsingType.Read
                    name = "sections_"
                }
            }
        }
    }

    // Remove ads sections from home.
    getDexMethod("homeStructureGetSectionsFingerprint") {
        structureGetSectionsFingerprint("homeapi.proto.HomeStructure")
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val sections = param.result
            // Set sections mutable
            sections.javaClass.findFirstFieldByExactType(Boolean::class.java).set(sections, true)
            UnlockPremiumPatch.removeHomeSections(param.result as MutableList<*>)
        }
    })
    // Remove ads sections from browser.
    getDexMethod("browseStructureGetSectionsFingerprint") {
        structureGetSectionsFingerprint("browsita.v1.resolved.BrowseStructure")
    }.hookMethod(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            val sections = param.result
            // Set sections mutable
            sections.javaClass.findFirstFieldByExactType(Boolean::class.java).set(sections, true)
            UnlockPremiumPatch.removeBrowseSections(param.result as MutableList<*>)
        }
    })

    // Remove pendragon (pop up ads) requests and return the errors instead.
    val replaceFetchRequestSingleWithError = object : XC_MethodHook() {
        val justMethod =
            DexMethod("Lio/reactivex/rxjava3/core/Single;->just(Ljava/lang/Object;)Lio/reactivex/rxjava3/core/Single;").toMethod()

        val onErrorField =
            DexField("Lio/reactivex/rxjava3/internal/operators/single/SingleOnErrorReturn;->b:Lio/reactivex/rxjava3/functions/Function;").toField()

        override fun afterHookedMethod(param: MethodHookParam) {
            if (!param.result.javaClass.name.endsWith("SingleOnErrorReturn")) return
            val justError = justMethod.invoke(null, onErrorField.get(param.result))
            param.result = justError
        }
    }

    getDexMethod("pendragonJsonFetchMessageRequestFingerprint") {
        findMethod {
            matcher {
                name("apply")
                addInvoke {
                    name("<init>")
                    declaredClass("FetchMessageRequest", StringMatchType.EndsWith)
                }
            }
        }.single()
    }.hookMethod(replaceFetchRequestSingleWithError)

    getDexMethod("pendragonJsonFetchMessageListRequestFingerprint") {
        findMethod {
            matcher {
                name("apply")
                addInvoke {
                    name("<init>")
                    declaredClass("FetchMessageListRequest", StringMatchType.EndsWith)
                }
            }
        }.single()
    }.hookMethod(replaceFetchRequestSingleWithError)
}

fun SpotifyHook.FixThirdPartyLaunchersWidgets() {
    getDexMethod("canBindAppWidgetPermissionFingerprint"){
        fingerprint {
            strings("android.permission.BIND_APPWIDGET")
            opcodes(Opcode.AND_INT_LIT8)
        }
    }.hookMethod(XC_MethodReplacement.returnConstant(true))
}
