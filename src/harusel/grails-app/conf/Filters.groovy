import security.UserContextHolder

class Filters {
    def filters = {
        hibernateFilter(controller: '*', action: '*') {
            after = {
                def user = UserContextHolder.contextUser
                user?.discard()
            }
        }
    }
}