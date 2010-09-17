scenario "Perspective chooser is correct on candidate view page", {
    given "PM is logged"
    when "The candidate view page is loaded"
    then "Perspective chooser is exists"
    and "Candidate review perspective is selected"
    and "There are Report and PR perspectives in the perspective chooser"
}

scenario "Perspective chooser is correct on performance review page", {
    given "PM is logged"
    when "The performance review page is loaded"
    then "Perspective chooser is exists"
    and "Performance review perspective is selected"
    and "There are Report and candidate view perspectives in the perspective chooser"
}

scenario "Perspective chooser is correct on reports page", {
    given "PM is logged"
    when "The Reports page is loaded"
    then "Perspective chooser is exists"
    and "Report perspective is selected"
    and "There are Candidate view and PR perspectives in the perspective chooser"
}

