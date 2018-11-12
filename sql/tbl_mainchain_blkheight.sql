DROP TABLE IF EXISTS `tbl_mainchain_blkheight`;
CREATE TABLE `tbl_mainchain_blkheight` (
  `blkheight` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`blkheight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;