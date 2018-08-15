CREATE SCHEMA botdiril;
USE botdiril;

CREATE TABLE IF NOT EXISTS users (
	pid INT AUTO_INCREMENT,
    userid BIGINT UNIQUE NOT NULL,
    PRIMARY KEY (pid)
);

CREATE TABLE IF NOT EXISTS remindmes (
	userid INT NOT NULL,
    remindwhen BIGINT NOT NULL,
    remindwhat VARCHAR(100) NOT NULL,
    CONSTRAINT remindmes_fkuser FOREIGN KEY (userid) REFERENCES users(pid)
);

CREATE TABLE IF NOT EXISTS properties (
	userid INT NOT NULL,
    propid VARCHAR(32) NOT NULL,
    propvalue INT NOT NULL,
    CONSTRAINT properties_fkuser FOREIGN KEY (userid) REFERENCES users(pid)
);

CREATE TABLE IF NOT EXISTS timers (
	userid INT NOT NULL,
    timertime BIGINT NOT NULL,
    timerid VARCHAR(32) NOT NULL,
    CONSTRAINT timers_fkuser FOREIGN KEY (userid) REFERENCES users(pid)
);

CREATE TABLE IF NOT EXISTS coins (
	userid INT NOT NULL,
    amount BIGINT NOT NULL,
    CONSTRAINT coinscoins_fkuser FOREIGN KEY (userid) REFERENCES users(pid),
    PRIMARY KEY (userid)
);

-- It is extremely important not to delete the contents of this table because every user would end up having completely different items if there was a new item
CREATE TABLE IF NOT EXISTS itemlookup (
    itemid INT PRIMARY KEY AUTO_INCREMENT,
    itemname VARCHAR(64) NOT NULL UNIQUE,
    itemtype VARCHAR(32) NOT NULL,
    itemprice BIGINT NOT NULL,
    itemsellvalue BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
	userid INT NOT NULL,
    itemid INT NOT NULL,
    itemcount BIGINT NOT NULL,
    CONSTRAINT inv_fkuser FOREIGN KEY (userid) REFERENCES users(pid),
    CONSTRAINT inv_fkitem FOREIGN KEY (itemid) REFERENCES itemlookup(itemid)
);

CREATE TABLE IF NOT EXISTS turnedoff (
	channelid BIGINT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS moderations (
	caseid INT AUTO_INCREMENT,
	userid INT NOT NULL,
    moderator BIGINT NOT NULL,
    mexpires BIGINT,
    mnote NVARCHAR(4096) NOT NULL,
    mtype TINYINT NOT NULL,
    messageid BIGINT,
    CONSTRAINT pun_fkuser FOREIGN KEY (userid) REFERENCES users(pid),
    PRIMARY KEY (caseid)
);

CREATE TABLE IF NOT EXISTS messages (
	userid INT NOT NULL,
    messageid BIGINT NOT NULL,
    messagecontent VARCHAR(2000) NOT NULL,
    CONSTRAINT mes_fkuser FOREIGN KEY (userid) REFERENCES users(pid)
);