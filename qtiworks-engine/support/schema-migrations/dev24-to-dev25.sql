ALTER TABLE candidate_events DROP playback_xeid;
ALTER TABLE item_delivery_settings DROP allow_playback;
DELETE FROM candidate_events WHERE item_event_type = 'PLAYBACK';
