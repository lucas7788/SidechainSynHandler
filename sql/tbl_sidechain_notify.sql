DROP TABLE IF EXISTS `tbl_sidechain_notify`;
CREATE TABLE `tbl_sidechain_notify` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `TxHash` varchar(64) NOT NULL,
  `blkHeight` int(11) NOT NULL DEFAULT 0,
  `funcName` varchar(64) DEFAULT '',
  `sideChainId` varchar(64) DEFAULT '',
  `address` varchar(64) DEFAULT '',
  `amount` varchar(64) DEFAULT '',
  `ContractAddress` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;