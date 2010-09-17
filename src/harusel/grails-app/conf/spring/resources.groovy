import security.LdapAuthProvider
import common.HRToolConfig
import security.FakeAuthProvider
import service.NotificationBuilder
import security.HRToolUserDetailsService

// Place your Spring DSL code here
beans = {

    // LDAP authentication provider
    ldapAuthProvider(LdapAuthProvider) {
        appUserService = ref('appUserService')
    }

    fakeAuthProvider(FakeAuthProvider) {
        appUserService = ref('appUserService')
    }

    userDetailsService(HRToolUserDetailsService) {
        appUserService = ref('appUserService')
    }

    mailSender(org.springframework.mail.javamail.JavaMailSenderImpl) {
        host = "${HRToolConfig.config.email.host}"
        port = "${HRToolConfig.config.email.port}"
        javaMailProperties = [
            'mail.smtp.localhost': 'localhost'
        ]
    }

// You can set default email bean properties here, eg: from/to/subject
    mailMessage(org.springframework.mail.SimpleMailMessage) {
        from = "${HRToolConfig.config['email.from']}"
    }

    notificationBuilder(NotificationBuilder) {
        messageSource = ref('messageSource')
        appUserService = ref('appUserService')
    }
}
