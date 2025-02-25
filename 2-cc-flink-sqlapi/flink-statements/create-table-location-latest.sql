CREATE TABLE `demo_fleet_mgmt_location_latest` (
  `vehicle_id` INT PRIMARY KEY NOT ENFORCED,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `driver_name` STRING NOT NULL,
  `license_plate` STRING NOT NULL
) DISTRIBUTED BY HASH(`vehicle_id`) INTO 3 BUCKETS;
