scenario "Open performance review by PM", {
    given "PM is logged"
    when "Perspective PR is selected in perspective chooser"
    then "Perspective PR is opened"
    and "The table with given PM's employers is displayed with timelines opposite names"
    and "The marks on the timelines are shown with accordance to the date and employers"
}

scenario "Review filled PR by PM", {
    given "PM is logged in the system"
    and "PR perspective is loaded"
    when "PM clicks mark on the timeline"
    then "PR anketa is loaded on new window"
    and "There are input boxes to make 'comments' and 'follow-up' for each answers"
}

scenario "Storing PR comments and follow-up's", {
    given "PM is logged"
    and "PR anketa is displayed"
    and "Some comments and follow-up's was added or/and edited"
    when "PM clicks button 'Save'"
    then "New comments and follow-up's are stored in DB"
    and "PR is closed"
}

scenario "Canceling PR comments and follow-up's", {
    given "PM is logged"
    and "PR anketa is displayed"
    and "Some comments and follow-up's was added or/and edited"
    when "PM clicks button 'Cancel'"
    then "New comments and follow-up's are restored in DB"
    and "PR is updated with old comments and follow-up's"
}

scenario "Displaying tooltip for marks on timeline", {
    given "PM is logged"
    and "PR perspective page is loaded"
    when "PM hovers mark on timeline"
    then "Tooltip with PR date is displayed"
}

scenario "Export PR", {
    given "PM is logged"
    and "PR perspective page is loaded"
    when "'Export' link is clicked"
    then "CSV file with all filled PR is downloaded"
}


