CREATE TABLE `ImageStorage` (
	`id` INT(11) NOT NULL AUTO_INCREMENT,
	`image_path` VARCHAR(350) NOT NULL,
	`image_name` VARCHAR(50) NOT NULL,
	`author_unique_user_id` VARCHAR(23) NOT NULL,
	`created_at` DATETIME NULL DEFAULT NULL,
	`rating` INT(11) NOT NULL DEFAULT '-1',
	PRIMARY KEY (`id`),
	INDEX `author_unique_user_id` (`author_unique_user_id`),
	CONSTRAINT `author_unique_user_id` FOREIGN KEY (`author_unique_user_id`) REFERENCES `users` (`unique_id`)
)
COMMENT='Store image path in MySQL database'
COLLATE='utf8_general_ci'
ENGINE=InnoDB
AUTO_INCREMENT=0
;
