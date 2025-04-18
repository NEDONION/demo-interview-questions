-- 车辆信息表：记录车辆基本属性
CREATE TABLE vehicle
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,            -- 主键，自增ID（系统内部唯一标识）
    plate_number VARCHAR(20)                  NOT NULL UNIQUE, -- 车牌号，唯一约束
    type         ENUM ('CAR', 'VAN', 'TRUCK') NOT NULL,        -- 车辆类型（小车、面包车、货车）
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP            -- 创建时间
);


-- 停车场每一层的信息表
CREATE TABLE parking_lot
(
    id              INT PRIMARY KEY AUTO_INCREMENT,    -- 主键，自增ID
    name            VARCHAR(100),                      -- 停车场名称（可选，例如“地下车库A”）
    level           INT NOT NULL,                      -- 楼层编号（如 1 表示一层，2 表示二层）
    total_spots     INT NOT NULL,                      -- 该层车位总数
    available_spots INT NOT NULL,                      -- 当前可用车位数（程序维护）
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP -- 记录创建时间
);

-- 停车票据表：记录每一次车辆进出场的完整信息
CREATE TABLE ticket
(
    id         CHAR(36) PRIMARY KEY, -- UUID 或 INT，自行选用
    vehicle_id VARCHAR(20) NOT NULL, -- 车牌号或车辆 ID
    level      INT NOT NULL,         -- 停在哪一层（楼层编号）
    entry_time DATETIME    NOT NULL, -- 入场时间
    exit_time  DATETIME,             -- 离场时间（可为空）
    price      DECIMAL(10, 2)        -- 离场时计算出的费用
);
