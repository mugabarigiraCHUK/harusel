import common.HRToolConfig
import ldap.LdapUser

//grails.app.context = HRToolConfig.config.appContext

// How many days after sheet saving user can edit this sheet
score.available.editable.days = 3

createDevelopmentData = true

// email used for testing notification service
notification.testEmail = 'text@text.com'

person {
    timeline.events.count.step = 10
    scores.date.format = 'MMM dd, yyyy'
    documents.to.upload = 1
}

// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

grails.config.locations = [
//         "classpath:${appName}-config.properties",
//         "classpath:${appName}-config.groovy",
//         "file:${userHome}/.grails/${appName}-config.properties",
//         "file:${userHome}/.grails/${appName}-config.groovy"
]
String configFileName = System.properties['external.config'] ?: 'externalConfig.groovy'
if (new File(configFileName).exists()) {
    grails.config.locations << 'file:' + configFileName
}

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
    xml: ['text/xml', 'application/xml'],
    text: 'text/plain',
    js: 'text/javascript',
    rss: 'application/rss+xml',
    atom: 'application/atom+xml',
    css: 'text/css',
    csv: 'text/csv',
    all: '*/*',
    json: ['application/json', 'text/json'],
    form: 'application/x-www-form-urlencoded',
    multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://hrtool"
    }
    development {
        grails.serverURL = "http://localhost:8080"
        // disable UI Performance in development.
        uiperformance.enabled = false
        cachefilter.filters.statics.enabled = false
//        uiperformance.enabled = true
    }

    mysql {
        uiperformance.enabled = false
        cachefilter.filters.statics.enabled = false
    }

    test {
        grails.serverURL = "http://localhost:8080"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error 'org.codehaus.groovy.grails.web.pages', //  GSP
        'org.codehaus.groovy.grails.web.sitemesh', //  layouts
        'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
        'org.codehaus.groovy.grails.web.mapping', // URL mapping
        'org.codehaus.groovy.grails.commons', // core / classloading
        'org.codehaus.groovy.grails.plugins', // plugins
        'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
        'org.springframework'
//    ,'org.hibernate'

    error stdout: "StackTrace"

    warn 'org.mortbay.log'

    info 'service',
        'org.springframework.security'

    debug 'grails.app',
        'migration',
        'domain',
        'security',
//        'org.codehaus.groovy.grails.web.servlet',  //  controllers
//        'org.hibernate.pretty.Printer',
//        'org.hibernate.loader'

//    trace 'org.hibernate.type'

        appenders {
            file(
                name: 'hrToolAppender',
                file: HRToolConfig.config.log.file as String,
                layout: pattern(conversionPattern: '%d %-5p %m%n')
            )
        }

    debug hrToolAppender: 'grails.app'
}

ldap = [
    directories: [
        ldapUser: HRToolConfig.config.ldap
    ],
    schemas: [
        LdapUser
    ]
]

//log4j.logger.org.springframework.security='off,stdout'

// Settings for UI Performance plugin
uiperformance = [
    // enable plugin.
    enabled: true,
    // Should we process images.
    processImages: true,
    // Should we keep original files.
    keepOriginals: true,

    // gzip for dynamic text content
    html: [
        compress: true,
        compressionThreshold: 1024,
        // compress all controllers urls. These are specified to prevent zipping Melody plugin output
        urlPatterns: ['/js/*', '/css/*', '/images/*', '/home/*', '/review/*', '/reports/*', '/document/*', '/person/*', '/score/*',
            '/employee/*', '/preferences/*', '/criterion/*'],
    ],
    // define bundles.
    bundles: [
        [
            type: 'js',
            name: 'jquery/jquery.all',
            files: [
                'jquery/jquery-1.3.2',
                'jquery/jquery.bgiframe',
                'jquery/jquery-ui-1.7.2.custom.min',
                'jquery/jquery.hoverIntent.minified',
                'jquery/jquery.bt',
                'jquery/autoresize/autoresize.jquery.min',
                'jquery/jquery.livequery',
                'jquery/jquery.blockUI',
                'jquery/jquery.form',
                'jquery/jquery.json-1.3',
                'json2',
                'jquery/jquery.text-overflow',
                'jquery/jsTree/css',
            ]
        ],

        [
            type: 'js',
            name: 'javascript-bundle',
            files: [
                'checkBoxBehaviour',
                'ckeditor/adapters/jquery',
                'highlighter',
                'application',
                'selectionManager',
                'person',
                'filters',
                'sendMail',
                'toolbar',
                'search',
                'scores',
                'document',
                'tooltip',
                'explorer',
                'preference',
                'perspective',
                'review',
                'assignedTemplates',
                'editTemplate',
                'reports',
                'anketa',
            ]
        ],

        [
            type: 'js',
            name: 'performanceReview-bundle',
            files: [
                'application',
                'anketa',
            ]
        ],

        [
            type: 'css',
            name: 'bundled',
            files: [
                'jquery-ui-1.7.2.custom',
                'tree_component',
                'main',
                'timeline',
            ]
        ]
    ]
]


cachefilter = [
    filters: [
        statics: [
            enabled: false,
            pattern: "*.js,*.css,*.png,*.ico,*.jpg,*.gif",
            lastModified: "initial",
            'max-age': 2592000, // 1 month
        ]
    ]
]

/**
 * This mapping is used by LdapUser class generator Script (setup-ldap)
 */
ldapMapping = [
    login: "sAMAccountName",
    fullName: "name",
    email: "mail",
    memberOf: "memberOf",
    accountState: "userAccountControl",
    disabledBitMask: 2,
]


userRolesDescription = [
    HR: [
        description: 'Human resources manager',
        ldapName: "CN=HR,${HRToolConfig.config.ldap.base}",
    ],
    PM: [
        description: 'Manager',
        ldapName: "CN=PM,${HRToolConfig.config.ldap.base}",
    ],
    USER: [
        description: 'Employee',
        ldapName: "CN=Users,${HRToolConfig.config.ldap.base}",
    ]
]

vacanciesToCreateOnEmptyDB = [
    'C/C++ developer',
    'C/C++ architect',
    'Java developer',
    'Java architect',
    'Manager',
    'Web designer',
    'Tester'
]

sourcesToCreateOnEmptyDB = [
    'NGS',
    'Мой круг',
    'Иван Сухоруков',
    'e-Rabota'
]

stageNameMap = [
    "newPerson",
    "resumeWaiting",
    "resumeReading",
    "questionnaireWaiting",
    "questionnaireReading",
    "interview",
    "companyDecisionWaiting",
    "infoWaiting",
    "repeatedInterview",
    "inviteAcceptanceWaiting",
    "answerWaiting",
    "workerWaiting",
    "trial",
    "employee",
    "rejected",
    "paused",
    "abandon",
    "fired",
    "testing"
]

toolbarButtons = [
    [actionName: "reject", disabledOnStages: ["fired", "abandon", "rejected",], targetStage: "rejected",],
    [actionName: "pause", disabledOnStages: ["paused", "fired", "abandon",], targetStage: "paused",],
    [actionName: "sendQuestionnaire", disabledOnStages: ["questionnaireWaiting", "questionnaireReading",], targetStage: "questionnaireWaiting",],
    [actionName: "getInterview", disabledOnStages: ["interview",], targetStage: "interview",],
    [actionName: "accept", disabledOnStages: ["workerWaiting", "answerWaiting", "trial", "employee",], targetStage: "workerWaiting",],
    [actionName: "requestInfo", disabledOnStages: ["infoWaiting",], targetStage: "infoWaiting",],
]

personFilters = [
    [
        // this code value is used to find filter. This filter is used to gather statistics
        code: "inProgress",
        name: "Рассматриваются",
        description: "Рассматриваются",
        includedStageKeys: [
            "newPerson",
            "resumeWaiting",
            "resumeReading",
            "questionnaireWaiting",
            "questionnaireReading",
            "interview",
            "companyDecisionWaiting",
            "infoWaiting",
            "repeatedInterview",
            "inviteAcceptanceWaiting",
            "testing",
        ],
    ],
    [
        code: "accepted",
        name: "Принятые",
        description: "Принятые",
        includedStageKeys: [
            "workerWaiting",
            "trial",
            "employee",
        ],
    ],
    [
        code: "suspended",
        name: "Отложенные",
        description: "Отложенные",
        includedStageKeys: ["paused",],
    ],
    [
        code: "rejected",
        name: "Отклоненные",
        description: "Отклоненные",
        includedStageKeys: [
            "rejected",
            "abandon",
        ],
    ],
    [
        code: "fired",
        name: "Уволенные",
        description: "Уволенные",
        includedStageKeys: ["fired",],
    ],
]

docTypes = [
    [typeCode: 'questionnaire',],
    [typeCode: 'resumeRu'],
    [typeCode: 'resumeEn'],
]

createDummyPersons = true;

