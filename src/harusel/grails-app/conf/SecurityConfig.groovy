security {

    /** enable/disable security module          */
    active = true
    defaultTargetUrl = '/home'
    alwaysUseDefaultTargetUrl = false

    /** rememberMeServices   */
    cookieName = 'grails_remember_me'
    alwaysRemember = false
    tokenValiditySeconds = 1209600 //14 days
    parameter = '_spring_security_remember_me'
    rememberMeKey = 'grailsRocks'

    /** Don't use domain class for URL->Role mapping          */
    useRequestMapDomainClass = false
    useControllerAnnotations = true

    controllerAnnotationStaticRules = [
            '/images/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/js/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/css/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/login/**': ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/**': ['IS_AUTHENTICATED_FULLY']
    ]

    /** Use our authentication provider          */
//    providerNames = ['ldapAuthProvider']
    providerNames = ['fakeAuthProvider']

    useLogger = true

    // user caching
    cacheUsers = true

    authorities = [
            ROLE_EMPLOYEE_ALLOWED: 'General rule for employee specific staff',
            ROLE_PM_ALLOWED: 'General rule for PM specific staff',
            ROLE_HR_ALLOWED: 'General rule for HR specific staff',

            ROLE_SET_STAGE: 'Set stage',
            ROLE_SEND_EMAIL: 'Send email to persion',
            ROLE_ACCESS_PERSON_SOURCE: 'Access and modification of person source',
            ROLE_ACCESS_PERSON_VACANCY: 'Access and modification of person vacancies',
            ROLE_ACCESS_PREFERENCES: 'Access to preferences page',

            // quiz - performance review questions form
            ROLE_ACCESS_QUIZ_ASSIGN_FORM: 'Access to quiz assigning form',
            ROLE_ACCESS_QUIZ_MANAGE_FORM: 'Access to quiz management form',
            ROLE_ACCESS_QUIZ: 'Access to own quiz blank form',
            ROLE_ACCESS_QUIZ_RESULTS: 'Access to quiz results'
    ]


    rules {
        HR = [
                'ROLE_HR_ALLOWED',
                'ROLE_SET_STAGE',
                'ROLE_SEND_EMAIL',
                'ROLE_ACCESS_PERSON_SOURCE',
                'ROLE_ACCESS_QUIZ_ASSIGN_FORM',
                'ROLE_ACCESS_QUIZ_MANAGE_FORM',
                'ROLE_ACCESS_QUIZ_RESULTS',
                'ROLE_ACCESS_PERSON_VACANCY',
                'ROLE_ACCESS_PREFERENCES',
                'ROLE_EMPLOYEE_ALLOWED',
        ]

        PM = [
                'ROLE_PM_ALLOWED',
                'ROLE_ACCESS_QUIZ',
                'ROLE_ACCESS_QUIZ_RESULTS',
                'ROLE_EMPLOYEE_ALLOWED',
        ]

        USER = [
                'ROLE_ACCESS_QUIZ',
                'ROLE_EMPLOYEE_ALLOWED',
        ]
    }
}
