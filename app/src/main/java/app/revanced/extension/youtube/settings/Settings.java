package app.revanced.extension.youtube.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static app.revanced.extension.shared.settings.Setting.parent;
import static app.revanced.extension.shared.settings.Setting.parentsAny;
import static app.revanced.extension.youtube.sponsorblock.objects.CategoryBehaviour.IGNORE;
import static app.revanced.extension.youtube.sponsorblock.objects.CategoryBehaviour.MANUAL_SKIP;
import static app.revanced.extension.youtube.sponsorblock.objects.CategoryBehaviour.SKIP_AUTOMATICALLY;
import static app.revanced.extension.youtube.sponsorblock.objects.CategoryBehaviour.SKIP_AUTOMATICALLY_ONCE;

import app.revanced.extension.shared.settings.BaseSettings;
import app.revanced.extension.shared.settings.BooleanSetting;
import app.revanced.extension.shared.settings.EnumSetting;
import app.revanced.extension.shared.settings.FloatSetting;
import app.revanced.extension.shared.settings.IntegerSetting;
import app.revanced.extension.shared.settings.LongSetting;
import app.revanced.extension.shared.settings.StringSetting;
import app.revanced.extension.youtube.sponsorblock.SponsorBlockSettings;
import app.revanced.extension.youtube.swipecontrols.SwipeControlsConfigurationProvider.SwipeOverlayStyle;

@SuppressWarnings("deprecation")
public class Settings extends BaseSettings {
    // Ads
    public static final BooleanSetting HIDE_FULLSCREEN_ADS = new BooleanSetting("revanced_hide_fullscreen_ads", TRUE);
    public static final BooleanSetting HIDE_BUTTONED_ADS = new BooleanSetting("revanced_hide_buttoned_ads", TRUE);
    public static final BooleanSetting HIDE_GENERAL_ADS = new BooleanSetting("revanced_hide_general_ads", TRUE);
    public static final BooleanSetting HIDE_GET_PREMIUM = new BooleanSetting("revanced_hide_get_premium", TRUE);
    public static final BooleanSetting HIDE_HIDE_LATEST_POSTS = new BooleanSetting("revanced_hide_latest_posts_ads", TRUE);
    public static final BooleanSetting HIDE_MERCHANDISE_BANNERS = new BooleanSetting("revanced_hide_merchandise_banners", TRUE);
    public static final BooleanSetting HIDE_PAID_PROMOTION_LABEL = new BooleanSetting("revanced_hide_paid_promotion_label", TRUE);
    public static final BooleanSetting HIDE_PRODUCTS_BANNER = new BooleanSetting("revanced_hide_products_banner", TRUE);
    public static final BooleanSetting HIDE_PLAYER_STORE_SHELF = new BooleanSetting("revanced_hide_player_store_shelf", TRUE);
    public static final BooleanSetting HIDE_SHOPPING_LINKS = new BooleanSetting("revanced_hide_shopping_links", TRUE);
    public static final BooleanSetting HIDE_SELF_SPONSOR = new BooleanSetting("revanced_hide_self_sponsor_ads", TRUE);
    public static final BooleanSetting HIDE_VIDEO_ADS = new BooleanSetting("revanced_hide_video_ads", TRUE, true);
    public static final BooleanSetting HIDE_VISIT_STORE_BUTTON = new BooleanSetting("revanced_hide_visit_store_button", TRUE);
    public static final BooleanSetting HIDE_WEB_SEARCH_RESULTS = new BooleanSetting("revanced_hide_web_search_results", TRUE);
    // Uncategorized layout related settings.  Do not add to this section, and instead move these out and categorize them.
    public static final BooleanSetting HIDE_MOVIES_SECTION = new BooleanSetting("revanced_hide_movies_section", TRUE);
    public static final BooleanSetting HIDE_END_SCREEN_STORE_BANNER = new BooleanSetting("revanced_hide_end_screen_store_banner", TRUE);
    // Navigation buttons
    public static final BooleanSetting HIDE_HOME_BUTTON = new BooleanSetting("revanced_hide_home_button", FALSE, true);
    public static final BooleanSetting HIDE_CREATE_BUTTON = new BooleanSetting("revanced_hide_create_button", TRUE, true);
    public static final BooleanSetting HIDE_SHORTS_BUTTON = new BooleanSetting("revanced_hide_shorts_button", TRUE, true);
    public static final BooleanSetting HIDE_SUBSCRIPTIONS_BUTTON = new BooleanSetting("revanced_hide_subscriptions_button", FALSE, true);
    public static final BooleanSetting HIDE_NAVIGATION_BUTTON_LABELS = new BooleanSetting("revanced_hide_navigation_button_labels", FALSE, true);
    public static final BooleanSetting HIDE_NOTIFICATIONS_BUTTON = new BooleanSetting("revanced_hide_notifications_button", FALSE, true);
    public static final BooleanSetting SWITCH_CREATE_WITH_NOTIFICATIONS_BUTTON = new BooleanSetting("revanced_switch_create_with_notifications_button", TRUE, true,
            "revanced_switch_create_with_notifications_button_user_dialog_message");
    public static final BooleanSetting DISABLE_TRANSLUCENT_STATUS_BAR = new BooleanSetting("revanced_disable_translucent_status_bar", FALSE, true,
            "revanced_disable_translucent_status_bar_user_dialog_message");
    public static final BooleanSetting DISABLE_TRANSLUCENT_NAVIGATION_BAR_LIGHT = new BooleanSetting("revanced_disable_translucent_navigation_bar_light", FALSE, true);
    public static final BooleanSetting DISABLE_TRANSLUCENT_NAVIGATION_BAR_DARK = new BooleanSetting("revanced_disable_translucent_navigation_bar_dark", FALSE, true);

    // General layout
    public static final BooleanSetting SETTINGS_SEARCH_HISTORY = new BooleanSetting("revanced_settings_search_history", TRUE, true);
    public static final StringSetting SETTINGS_SEARCH_ENTRIES = new StringSetting("revanced_settings_search_entries", "", true);

    // Swipe controls
    public static final BooleanSetting SWIPE_CHANGE_VIDEO = new BooleanSetting("revanced_swipe_change_video", FALSE, true);
    public static final BooleanSetting SWIPE_BRIGHTNESS = new BooleanSetting("revanced_swipe_brightness", FALSE, true);
    public static final BooleanSetting SWIPE_VOLUME = new BooleanSetting("revanced_swipe_volume", FALSE, true);
    public static final BooleanSetting SWIPE_PRESS_TO_ENGAGE = new BooleanSetting("revanced_swipe_press_to_engage", FALSE, true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final BooleanSetting SWIPE_HAPTIC_FEEDBACK = new BooleanSetting("revanced_swipe_haptic_feedback", TRUE, true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final IntegerSetting SWIPE_MAGNITUDE_THRESHOLD = new IntegerSetting("revanced_swipe_threshold", 30, true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final IntegerSetting SWIPE_VOLUME_SENSITIVITY = new IntegerSetting("revanced_swipe_volume_sensitivity", 1, true, parent(SWIPE_VOLUME));
    public static final EnumSetting<SwipeOverlayStyle> SWIPE_OVERLAY_STYLE = new EnumSetting<>("revanced_swipe_overlay_style", SwipeOverlayStyle.HORIZONTAL,true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final IntegerSetting SWIPE_OVERLAY_TEXT_SIZE = new IntegerSetting("revanced_swipe_text_overlay_size", 14, true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final IntegerSetting SWIPE_OVERLAY_OPACITY = new IntegerSetting("revanced_swipe_overlay_background_opacity", 60, true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final StringSetting SWIPE_OVERLAY_BRIGHTNESS_COLOR = new StringSetting("revanced_swipe_overlay_progress_brightness_color", "#FFFFFF", true,
            parent(SWIPE_BRIGHTNESS));
    public static final StringSetting SWIPE_OVERLAY_VOLUME_COLOR = new StringSetting("revanced_swipe_overlay_progress_volume_color", "#FFFFFF", true,
            parent(SWIPE_VOLUME));
    public static final LongSetting SWIPE_OVERLAY_TIMEOUT = new LongSetting("revanced_swipe_overlay_timeout", 500L, true,
            parentsAny(SWIPE_BRIGHTNESS, SWIPE_VOLUME));
    public static final BooleanSetting SWIPE_SAVE_AND_RESTORE_BRIGHTNESS = new BooleanSetting("revanced_swipe_save_and_restore_brightness", TRUE, true,
            parent(SWIPE_BRIGHTNESS));
    public static final FloatSetting SWIPE_BRIGHTNESS_VALUE = new FloatSetting("revanced_swipe_brightness_value", -1f);
    public static final BooleanSetting SWIPE_LOWEST_VALUE_ENABLE_AUTO_BRIGHTNESS = new BooleanSetting("revanced_swipe_lowest_value_enable_auto_brightness", FALSE, true,
            parent(SWIPE_BRIGHTNESS));

    // SponsorBlock
    public static final BooleanSetting SB_ENABLED = new BooleanSetting("sb_enabled", TRUE);
    /** Do not use id setting directly. Instead use {@link SponsorBlockSettings}. */
    public static final StringSetting SB_PRIVATE_USER_ID = new StringSetting("sb_private_user_id_Do_Not_Share", "");
    public static final IntegerSetting SB_CREATE_NEW_SEGMENT_STEP = new IntegerSetting("sb_create_new_segment_step", 150, parent(SB_ENABLED));
    public static final BooleanSetting SB_VOTING_BUTTON = new BooleanSetting("sb_voting_button", FALSE, parent(SB_ENABLED));
    public static final BooleanSetting SB_CREATE_NEW_SEGMENT = new BooleanSetting("sb_create_new_segment", FALSE, parent(SB_ENABLED));
    public static final BooleanSetting SB_SQUARE_LAYOUT = new BooleanSetting("sb_square_layout", FALSE, parent(SB_ENABLED));
    public static final BooleanSetting SB_COMPACT_SKIP_BUTTON = new BooleanSetting("sb_compact_skip_button", FALSE, parent(SB_ENABLED));
    public static final BooleanSetting SB_AUTO_HIDE_SKIP_BUTTON = new BooleanSetting("sb_auto_hide_skip_button", TRUE, parent(SB_ENABLED));
    public static final BooleanSetting SB_TOAST_ON_SKIP = new BooleanSetting("sb_toast_on_skip", TRUE, parent(SB_ENABLED));
    public static final BooleanSetting SB_TOAST_ON_CONNECTION_ERROR = new BooleanSetting("sb_toast_on_connection_error", TRUE, parent(SB_ENABLED));
    public static final BooleanSetting SB_TRACK_SKIP_COUNT = new BooleanSetting("sb_track_skip_count", TRUE, parent(SB_ENABLED));
    public static final FloatSetting SB_SEGMENT_MIN_DURATION = new FloatSetting("sb_min_segment_duration", 0F, parent(SB_ENABLED));
    public static final BooleanSetting SB_VIDEO_LENGTH_WITHOUT_SEGMENTS = new BooleanSetting("sb_video_length_without_segments", FALSE, parent(SB_ENABLED));
    public static final StringSetting SB_API_URL = new StringSetting("sb_api_url", "https://sponsor.ajay.app");
    public static final BooleanSetting SB_USER_IS_VIP = new BooleanSetting("sb_user_is_vip", FALSE);
    public static final IntegerSetting SB_LOCAL_TIME_SAVED_NUMBER_SEGMENTS = new IntegerSetting("sb_local_time_saved_number_segments", 0);
    public static final LongSetting SB_LOCAL_TIME_SAVED_MILLISECONDS = new LongSetting("sb_local_time_saved_milliseconds", 0L);
    public static final LongSetting SB_LAST_VIP_CHECK = new LongSetting("sb_last_vip_check", 0L, false, false);
    public static final BooleanSetting SB_HIDE_EXPORT_WARNING = new BooleanSetting("sb_hide_export_warning", FALSE, false, false);
    public static final BooleanSetting SB_SEEN_GUIDELINES = new BooleanSetting("sb_seen_guidelines", FALSE, false, false);
    public static final StringSetting SB_CATEGORY_SPONSOR = new StringSetting("sb_sponsor", SKIP_AUTOMATICALLY_ONCE.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_SPONSOR_COLOR = new StringSetting("sb_sponsor_color", "#00D400");
    public static final FloatSetting  SB_CATEGORY_SPONSOR_OPACITY = new FloatSetting("sb_sponsor_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_SELF_PROMO = new StringSetting("sb_selfpromo", MANUAL_SKIP.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_SELF_PROMO_COLOR = new StringSetting("sb_selfpromo_color", "#FFFF00");
    public static final FloatSetting  SB_CATEGORY_SELF_PROMO_OPACITY = new FloatSetting("sb_selfpromo_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_INTERACTION = new StringSetting("sb_interaction", MANUAL_SKIP.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_INTERACTION_COLOR = new StringSetting("sb_interaction_color", "#CC00FF");
    public static final FloatSetting  SB_CATEGORY_INTERACTION_OPACITY = new FloatSetting("sb_interaction_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_HIGHLIGHT = new StringSetting("sb_highlight", MANUAL_SKIP.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_HIGHLIGHT_COLOR = new StringSetting("sb_highlight_color", "#FF1684");
    public static final FloatSetting  SB_CATEGORY_HIGHLIGHT_OPACITY = new FloatSetting("sb_highlight_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_INTRO = new StringSetting("sb_intro", MANUAL_SKIP.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_INTRO_COLOR = new StringSetting("sb_intro_color", "#00FFFF");
    public static final FloatSetting  SB_CATEGORY_INTRO_OPACITY = new FloatSetting("sb_intro_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_OUTRO = new StringSetting("sb_outro", MANUAL_SKIP.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_OUTRO_COLOR = new StringSetting("sb_outro_color", "#0202ED");
    public static final FloatSetting  SB_CATEGORY_OUTRO_OPACITY = new FloatSetting("sb_outro_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_PREVIEW = new StringSetting("sb_preview", IGNORE.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_PREVIEW_COLOR = new StringSetting("sb_preview_color", "#008FD6");
    public static final FloatSetting  SB_CATEGORY_PREVIEW_OPACITY = new FloatSetting("sb_preview_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_FILLER = new StringSetting("sb_filler", IGNORE.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_FILLER_COLOR = new StringSetting("sb_filler_color", "#7300FF");
    public static final FloatSetting  SB_CATEGORY_FILLER_OPACITY = new FloatSetting("sb_filler_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_MUSIC_OFFTOPIC = new StringSetting("sb_music_offtopic", MANUAL_SKIP.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_MUSIC_OFFTOPIC_COLOR = new StringSetting("sb_music_offtopic_color", "#FF9900");
    public static final FloatSetting  SB_CATEGORY_MUSIC_OFFTOPIC_OPACITY = new FloatSetting("sb_music_offtopic_opacity", 0.8f);
    public static final StringSetting SB_CATEGORY_UNSUBMITTED = new StringSetting("sb_unsubmitted", SKIP_AUTOMATICALLY.reVancedKeyValue);
    public static final StringSetting SB_CATEGORY_UNSUBMITTED_COLOR = new StringSetting("sb_unsubmitted_color", "#FFFFFF");
    public static final FloatSetting  SB_CATEGORY_UNSUBMITTED_OPACITY = new FloatSetting("sb_unsubmitted_opacity", 1.0f);

    // Misc
    public static final BooleanSetting DEBUG_PROTOBUFFER = new BooleanSetting("revanced_debug_protobuffer", FALSE, false,
            "revanced_debug_protobuffer_user_dialog_message", parent(BaseSettings.DEBUG));
    public static final BooleanSetting AUTO_REPEAT = new BooleanSetting("revanced_auto_repeat", FALSE);
    public static final BooleanSetting REMOVE_TRACKING_QUERY_PARAMETER = new BooleanSetting("revanced_remove_tracking_query_parameter", TRUE);
}