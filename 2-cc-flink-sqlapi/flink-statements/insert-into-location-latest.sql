INSERT INTO `demo_fleet_mgmt_location_latest`
SELECT l.vehicle_id, l.location.latitude, l.location.longitude, d.driver_name, d.license_plate
  FROM `demo_fleet_mgmt_location` l
  INNER JOIN `demo_fleet_mgmt_description` d ON l.vehicle_id = d.vehicle_id;