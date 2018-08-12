create database event_server_mash
  charset utf8mb4;

create table if not exists event_task (
  `id`            int                   auto_increment,
  `region`        int          not null,
  `name`          varchar(255) not null,
  `data`          text         not null,
  `create_ip`     text         not null,
  `create_port`   int          not null,
  `source_event`  int          null,
  `front_event`   int          null,
  `result`        text         null,
  `time_enable`   datetime     not null default now(),
  `time_create`   datetime     not null default now(),
  `time_modified` datetime     not null default now(),
  `status`        int          not null,
  primary key (`id`)
)
  engine = InnoDB
  default charset utf8mb4;