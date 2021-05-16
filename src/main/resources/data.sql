-- Populate the `ACCOUNT` table
INSERT INTO `account` (`id`, `holder`, `balance`, `pin`) VALUES (1001, 'Stanley Parker',  500,  X'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');
INSERT INTO `account` (`id`, `holder`, `balance`, `pin`) VALUES (1002, 'Darren Atkinson', 1050, X'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');
INSERT INTO `account` (`id`, `holder`, `balance`, `pin`) VALUES (1003, 'Lizzie George',   750,  X'03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4');

-- Populate the `TRANSACTION` table
INSERT INTO `transaction` (`id`, `amount`, `payer`, `payer_balance`, `payee`, `payee_balance`, `timestamp`) VALUES (2001, 1050.00, null,    null, 1001, 1050.00, '2020-01-05 09:11:11.907Z');
INSERT INTO `transaction` (`id`, `amount`, `payer`, `payer_balance`, `payee`, `payee_balance`, `timestamp`) VALUES (2002,  450.00, 1001,  600.00, null,    null, '2020-02-13 15:18:49.771Z');
INSERT INTO `transaction` (`id`, `amount`, `payer`, `payer_balance`, `payee`, `payee_balance`, `timestamp`) VALUES (2003,  100.00, 1001,  500.00, 1003,  100.00, '2020-03-24 13:32:26.876Z');
INSERT INTO `transaction` (`id`, `amount`, `payer`, `payer_balance`, `payee`, `payee_balance`, `timestamp`) VALUES (2004,  650.00, null,    null, 1003,  750.00, '2020-04-10 12:45:56.006Z');
INSERT INTO `transaction` (`id`, `amount`, `payer`, `payer_balance`, `payee`, `payee_balance`, `timestamp`) VALUES (2005, 1050.00, null,    null, 1002, 1050.00, '2020-05-09 09:13:21.447Z');