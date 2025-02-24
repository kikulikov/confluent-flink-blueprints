CREATE TABLE `demo_fleet_mgmt_location_detailed` (
  `vehicle_id` INT NOT NULL,
  `latitude` DOUBLE NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `driver_name` STRING NOT NULL,
  `license_plate` STRING NOT NULL
) DISTRIBUTED BY HASH(`vehicle_id`) INTO 5 BUCKETS;
