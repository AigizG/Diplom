alter table activities
    add column short_description varchar(1000),
    add column gallery_images varchar(4000),
    add column included_services varchar(4000),
    add column not_included_services varchar(4000),
    add column route_description varchar(4000);

