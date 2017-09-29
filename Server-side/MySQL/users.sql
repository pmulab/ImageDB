CREATE TABLE `users` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`unique_id` VARCHAR(23) NOT NULL,
	`name` VARCHAR(50) NOT NULL,
	`email` VARCHAR(100) NOT NULL,
	`encrypted_password` VARCHAR(80) NOT NULL,
	`salt` VARCHAR(10) NOT NULL,
	`created_at` DATETIME NULL DEFAULT NULL,
	`updated_at` DATETIME NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	UNIQUE INDEX `unique_id` (`unique_id`),
	UNIQUE INDEX `email` (`email`)
)
COMMENT='User data.'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=4
;