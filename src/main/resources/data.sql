-- Populate the `ACCOUNT` table
INSERT INTO `account` (`id`, `holder`, `balance`, `pin`) VALUES (1001, 'Stanley Parker',  500,  X'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');
INSERT INTO `account` (`id`, `holder`, `balance`, `pin`) VALUES (1002, 'Darren Atkinson', 1050, X'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');
INSERT INTO `account` (`id`, `holder`, `balance`, `pin`) VALUES (1003, 'Lizzie George',   750,  X'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');

-- Populate the `TRANSACTION` table
INSERT INTO `transaction` (`id`, `amount`, `author_balance`, `target_balance`, `timestamp`, `author`, `target`) VALUES (2001, 1050.00, 1050.00,   null, '2020-01-05 09:11:11.907Z', 1001, null);
INSERT INTO `transaction` (`id`, `amount`, `author_balance`, `target_balance`, `timestamp`, `author`, `target`) VALUES (2002, -450.00,  600.00,   null, '2020-02-13 15:18:49.771Z', 1001, null);
INSERT INTO `transaction` (`id`, `amount`, `author_balance`, `target_balance`, `timestamp`, `author`, `target`) VALUES (2003, -100.00,  500.00, 100.00, '2021-03-24 13:32:26.876Z', 1001, 1003);
INSERT INTO `transaction` (`id`, `amount`, `author_balance`, `target_balance`, `timestamp`, `author`, `target`) VALUES (2004,  650.00,  750.00,   null, '2021-04-10 12:45:56.006Z', 1003, null);
INSERT INTO `transaction` (`id`, `amount`, `author_balance`, `target_balance`, `timestamp`, `author`, `target`) VALUES (2005,  650.00,  750.00,   null, '2021-04-10 12:45:56.006Z', 1003, null);
INSERT INTO `transaction` (`id`, `amount`, `author_balance`, `target_balance`, `timestamp`, `author`, `target`) VALUES (2006, 1000.00, 1000.00,   null, '2020-03-09 09:13:21.447Z', 1002, null);