ALTER TABLE `shows`
    ADD COLUMN
    `rating`       DECIMAL(3, 2)   NOT NULL    DEFAULT 0.00
;

ALTER TABLE `shows`
    ADD COLUMN
    `watched_all`     INTEGER         NOT NULL   DEFAULT 0
;
