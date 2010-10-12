CREATE INDEX stage_id_idx on person (stage_id);
CREATE INDEX source_id_idx on person (source_id);
CREATE INDEX vacancy_idx on person_vacancy (person_vacancies_id, vacancy_id);
create index person_read_flag_idx on read_flag (person_id);                 
create index user_read_flag_idx on read_flag (user_id);
create index person_vacancy_idx on person_vacancy (person_vacancies_id);
create index person_person_event_idx on person_event (person_id);
