--  add code name to old stages
update  stage set code_name='newPerson' where  substring(name,1,datalength(name)) = N'Новый'
update  stage set code_name='resumeWaiting' where  substring(name,1,datalength(name)) = N'Ожидание резюме'
update  stage set code_name='resumeReading' where  substring(name,1,datalength(name)) = N'Рассмотрение резюме'
update  stage set code_name='questionnaireWaiting' where  substring(name,1,datalength(name)) = N'Ожидание анкеты'
update  stage set code_name='questionnaireReading' where  substring(name,1,datalength(name)) = N'Рассмотрение анкеты'
update  stage set code_name='interview' where  substring(name,1,datalength(name)) = N'Приглашен на собеседование'
update  stage set code_name='companyDecisionWaiting' where  substring(name,1,datalength(name)) = N'Ожидание решения компании'
update  stage set code_name='infoWaiting' where  substring(name,1,datalength(name)) = N'Ожидание дополнительной информации'
update  stage set code_name='repeatedInterview' where  substring(name,1,datalength(name)) = N'Приглашён на 2й или 3й этап собеседования'
update  stage set code_name='inviteAcceptanceWaiting' where  substring(name,1,datalength(name)) = N'Ожидание подтверждения приглашения'
update  stage set code_name='answerWaiting' where  substring(name,1,datalength(name)) = N'Ожидание согласия кандидата'
update  stage set code_name='workerWaiting' where  substring(name,1,datalength(name)) = N'Ожидаем выхода на работу'
update  stage set code_name='rejected' where substring(name,1,datalength(name)) = N'Отклонен'
update  stage set code_name='paused' where substring(name,1,datalength(name)) = N'Отложен'
update  stage set code_name='abandon' where substring(name,1,datalength(name)) = N'Отказался'
-- new stages will be added on starting application

-- change DocumentType table
update  document_type set type_code='questionnaire', name='questionnaire' where substring(icon,1,datalength(icon)) = 'questionnaire.png'
update  document_type set type_code='resumeRu', name='resumeRu' where substring(icon,1,datalength(icon)) = 'cv_ru.png'
update  document_type set type_code='resumeEn', name='resumeEn' where substring(icon,1,datalength(icon)) = 'cv_en.png'

alter table document_type drop column icon
alter table stage drop column name
alter table app_user drop column managers
alter table comments drop column follow_up

drop table app_user_roles
drop table question_templates

-- migrate data from app_user_vacancies to vacancy_app_user table
insert into  vacancy_app_user (vacancy_users_id, app_user_id) (SELECT vacancy_id, app_user_id FROM app_user_vacancies)
drop table app_user_vacancies

-- migrate data from person_vacancies to person_vacancy table
insert into  person_vacancy ( person_vacancies_id, vacancy_id) (SELECT person_id, vacancy_id FROM person_vacancies)
drop table person_vacancies

-- required decisions
update person set required_decisions_flag=1 where id in (select person_id from required_decision_flag)
drop table required_decision_flag

-- role_right are filled on application start up
drop table role_rights

-- stage transitions are unused at all
drop table stage_transition


-- changing data types
ALTER TABLE stage ALTER COLUMN code_name nvarchar(30)