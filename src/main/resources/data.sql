-- ADD EXISTING USER
insert
    into
        card_user
        (id, emirates_id, expiry_date, name, status, created_on, last_modified_on)
    values
        (1, '784199123456789', '2027-10-01', 'Ali G', 1, '2024-09-09 10:00:00', '2024-09-09 10:00:00');
