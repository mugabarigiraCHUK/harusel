scenario "Perspective chooser is correct on candidate review page", {
    given "PM is logged"
    when "The candidate view page is loaded"

    then "Perspective chooser is exists"
    and "Candidate review perspective is selected"
    and "There are Report and PR perspectives in the perspective chooser"
}

scenario "Changing perspective from candidate review to performance review", {
    given "PM is logged"
    and "The candidate view page is loaded"
    and "Known PM's employer filled the PR on known date"
    when "Perspective PR is selected"

    then "Perspective PR is opened"
    and "The table with PM's employers is displayed with timelines opposite names"
    and "The marks on the timelines are shown with accordance to known date and employers"
}

scenario "Review filled PR by PM", {
    given "PM is logged in the system"
    and "PR perspective is loaded"
    when "PM clicks mark on the timeline"

    then "PR anketa is loaded on new window"
    and "There are input boxes to make 'comments' and 'follow-up' for each answers"
}

scenario "Storing PR comments and marks", {

}
