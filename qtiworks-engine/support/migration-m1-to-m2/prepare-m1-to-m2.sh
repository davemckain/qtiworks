# Updates production schema and trims unrequired data
dropdb qtiworks
createdb -O qtiworks qtiworks
psql -U qtiworks qtiworks < /tmp/qtiworks.sql
psql -U qtiworks qtiworks < support/migrate-m1-to-m2.sql

# TMP! Trims data from DEV to merge with support
dropdb qtiworks_dev
createdb -O qtiworks qtiworks_dev
psql -U qtiworks qtiworks_dev < /tmp/qtiworks_dev.sql
psql -U qtiworks qtiworks_dev < support/trim-dev-for-m2.sql
pg_dump -a qtiworks_dev \
  -t delivery_settings \
  -t item_delivery_settings \
  -t test_delivery_settings \
  -t assessments \
  -t assessment_packages \
  -t assessment_package_qti_files \
  -t assessment_package_safe_files \
  -t deliveries \
> /tmp/qtiworks-dev-for-import.sql
# NB: The need to do a bit of manual patching to remove duplicates, then copy to support/sue-import.sql

# Merge in...
psql -U qtiworks qtiworks < support/sue-import.sql
