#include <pebble.h>

static const uint8_t MSG_KEY_TYPE = (uint8_t) 1;

static const char *MSG_TYPE_ATTACK = "ATTACK";

static Window *window;
static TextLayer *top_text_layer;
static TextLayer *bottom_text_layer;
static GBitmap *paintballBitmap;

static void send_attack_message()
{
  DictionaryIterator *iter;

  app_message_outbox_begin(&iter);
  Tuplet messageTypeTuplet = TupletCString(MSG_KEY_TYPE,MSG_TYPE_ATTACK);

  dict_write_tuplet(iter,&messageTypeTuplet);
  app_message_outbox_send();
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context) {
  APP_LOG(APP_LOG_LEVEL_DEBUG,"Select Clicked");
  send_attack_message();
}

static void click_config_provider(void *context) {
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
}

static void window_load(Window *window) {
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);

  const uint32_t textHeight = 20;

  GRect topTextLocation = (GRect) { .origin = {0,0}, .size = { bounds.size.w, textHeight } };
  top_text_layer = text_layer_create(topTextLocation);
  text_layer_set_text(top_text_layer, "LIBERTY PAINTBALL");
  text_layer_set_text_alignment(top_text_layer, GTextAlignmentCenter);


  GRect bottomTextLocation = (GRect) { .origin = { 0,(bounds.size.h-textHeight) }, .size = { bounds.size.w, textHeight } };
  bottom_text_layer = text_layer_create(bottomTextLocation);
  text_layer_set_text(bottom_text_layer,"Select/Shake to fire!");
  text_layer_set_text_alignment(bottom_text_layer, GTextAlignmentCenter);
 
  layer_add_child(window_layer, text_layer_get_layer(top_text_layer));
  layer_add_child(window_layer, text_layer_get_layer(bottom_text_layer));
}

static void window_unload(Window *window) {
  text_layer_destroy(bottom_text_layer);
}

static void msg_out_sent_handler(DictionaryIterator *msgSent, void *context)
{
}

static void msg_out_failed_handler(DictionaryIterator *msgFailed, AppMessageResult reason, void *context)
{
}

static void msg_in_rec_handler(DictionaryIterator *msgRec, void *context)
{
}

static void msg_in_dropped_handler(AppMessageResult reason, void *context)
{
}

static void accel_tap_handler(AccelAxisType axis, int32_t direction)
{
  APP_LOG(APP_LOG_LEVEL_DEBUG,"Accelerometer tapped - sending attack message.");
  send_attack_message();
}

static void init(void)
{
  window = window_create();
  window_set_click_config_provider(window,click_config_provider);
  window_set_window_handlers(window, (WindowHandlers) {
    .load = window_load,
    .unload = window_unload,
  });
  const bool animated = true;
  window_stack_push(window, animated);

  Layer *window_layer = window_get_root_layer(window);

  paintballBitmap = gbitmap_create_with_resource(RESOURCE_ID_IMAGE_PAINTBALL);
  GRect screenBounds = layer_get_frame(window_layer);
  BitmapLayer *image_layer = bitmap_layer_create(screenBounds);

  bitmap_layer_set_bitmap(image_layer,paintballBitmap);
  bitmap_layer_set_alignment(image_layer, GAlignCenter);
  layer_add_child(window_layer,bitmap_layer_get_layer(image_layer));

  //setup messaging handlers
  app_message_register_outbox_sent(msg_out_sent_handler);
  app_message_register_outbox_failed(msg_out_failed_handler); 

  app_message_register_inbox_received(msg_in_rec_handler);
  app_message_register_inbox_dropped(msg_in_dropped_handler);

  const uint32_t inbound_box_size = 64;
  const uint32_t outbound_box_size = 64;

  app_message_open(inbound_box_size,outbound_box_size);


  //setup accelerometer
  const uint32_t samples_per_update = 1;
  accel_tap_service_subscribe(accel_tap_handler);
  
}

static void deinit(void) {
  window_destroy(window);
  gbitmap_destroy(paintballBitmap);
}

int main(void) {
  init();

  app_event_loop();
  deinit();
}
