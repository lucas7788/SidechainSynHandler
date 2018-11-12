DROP TABLE IF EXISTS `tbl_sendtx_detail`;
CREATE TABLE `tbl_sendtx_detail` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `txhash` varchar(64) Not NULL  DEFAULT  '',
  `blkHeight` int(11) NOT NULL DEFAULT 0,
  `amount` varchar(64) NOT NULL DEFAULT '',
  `chainType` varchar(64) NOT NULL DEFAULT '',
  `nodeUrl` varchar(64) NOT NULL DEFAULT '',
  `functionName` varchar(64) NOT NULL DEFAULT '',
  `sideChainId` varchar(64) NOT NULL DEFAULT '',
  `address` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
--   foreign key(txhash) references tbl_sendtx_msginfo(txhash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;