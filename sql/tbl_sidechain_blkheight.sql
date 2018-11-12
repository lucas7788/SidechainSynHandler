DROP TABLE IF EXISTS `tbl_sidechain_blkheight`;
CREATE TABLE `tbl_sidechain_blkheight` (
  `blkheight` int(11) NOT NULL,
  PRIMARY KEY (`blkheight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;