package service

import domain.user.AppUser
import performanceReview.Answer
import performanceReview.Form
import performanceReview.Question
import performanceReview.Template
import util.SecurityTools

class ReviewService {

    /**
     * Retrieves not deleted or with notpublished answers questions as list
     */
    def List<Question> getVisibleQuestions() {
        return Question.findAll("from Question as q where isDeleted=False or id in (select question from Answer where form.published = False)")
    }

    /**
     * Returns number of not published answers for given question
     */
    def countNotPublishedAnswers(Question question) {
        return Form.executeQuery("select count(a.id) from Answer a where a.form.published = :published and a.question.id = :questionId",
            [published: false, questionId: question.id])?.get(0)
    }

    /**
     * Retrives answers for the given date range for the given user.
     * @return If user is HR then all answers are returned,
     * if user is manager then answers from his employees are returned.
     */
    List<Answer> getPublicatedAnswersForPeriod(Date dateFrom, Date dateTo, AppUser manager) {
        if (SecurityTools.isHR(manager)) {
            return Answer.withCriteria {
                and {
                    form {
                        eq('published', true)
                        between('modificationDate', dateFrom, dateTo)
                        order('modificationDate', 'asc')
                    }
                }
            }
        }
        else {
            return Answer.withCriteria {
                and {
                    form {
                        eq('published', true)
                        between('modificationDate', dateFrom, dateTo)
                        user {
                            eq('manager', manager)
                        }
                        order('modificationDate', 'asc')
                    }
                }
            }
        }
    }

    def assignToUserManagerAndTemplate(def user, def managerId, def templateId) {
        if (templateId) {
            def template = Template.get(templateId as Long)
            if (!template) {
                throw new IllegalStateException("Can't find template with id '$templateId'")
            }
            log.debug("Assigning template '${template.name}' to user '${user.fullName}'")
            user.assignedTemplate = template;
        } else if (user.assignedTemplate) {
            log.debug("Deassign template '${user.assignedTemplate.name}' from user '${user.fullName}'")
            user.assignedTemplate = null;
        }
        if (managerId) {
            def manager = AppUser.get(managerId as Long)
            if (!manager) {
                throw new IllegalStateException("Can't find manager with id '$managerId'")
            }
            log.debug("Assiging manager '${manager.fullName}' to user '${user.fullName}'")
            user.manager = manager;
        } else if (user.manager) {
            log.debug("Deassiging manager '${user.manager.fullName}' from user '${user.fullName}'")
            user.manager = null;
        }
        user.save();
    }

}
