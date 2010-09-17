package reports

/**
 * Person events model is needed to count each event only one time
 */
class PersonEvents {
    // person was in negotiation state
    boolean processing = false
    // person was rejected
    boolean rejected = false
}
