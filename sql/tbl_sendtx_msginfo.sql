DROP TABLE IF EXISTS `tbl_sendtx_msginfo`;
CREATE TABLE `tbl_sendtx_msginfo` (
  `txhash` varchar(64) Not NULL  DEFAULT  '',
  `functionName` varchar(64) NOT NULL DEFAULT '',
  `nodeUrl` varchar(64) NOT NULL DEFAULT '',
  `result` varchar(64) NOT NULL DEFAULT '',
  `description` varchar(64) NOT NULL DEFAULT '',
  PRIMARY KEY (`txhash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;