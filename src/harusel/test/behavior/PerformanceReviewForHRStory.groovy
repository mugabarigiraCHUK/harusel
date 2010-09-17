scenario "Open performance review by HR", {
    given "HR is logged in the system"
    when "Perspective PR is selected in perspective chooser"
    then "Perspective PR is opened"
    and "Tab for managering PR is shown"
    and "Tab for managering PR templates is shown"
    and "Tab for PR report is shown"
}

scenario "Opening managering PR tab", {
    given "HR is logged in the system"
    when "PR perspective is loaded"
    then "Managering PR tab is selected"
    and "Table with all employers are displayed"
    and "PR templates selectors are linked to each employers"
}

scenario "Changing PR template to employers", {
    given "HR is logged in the system"
    and "Managering tab is opened in PR perspective"
    and "HR change PR template to employer"
    when "HR clicks button 'Save'"
    then "New PR template is linked to employer in DB"
}

scenario "Canceling selected PR template", {
    given "HR is logged in the system"
    and "Managering PR tab is opened in PR perspective"
    and "HR change PR template to employer"
    when "HR clicks button 'Cancel'"
    then "The page is reloaded with old data"
    and "DB hasn't been updated"
}

scenario "Opening Managering PR templates tab", {
    given "HR is logged in the system"
    and "PR perspective page is loaded"
    when "HR clicks on Managering PR templates tab"
    then "Managering PR templates tab is selected"

}

scenario "Review filled PR by HR", {
    given "HR is logged in the system"
    and "PR perspective is loaded"
    when "HR clicks mark on the timeline"
    then "PR anketa is loaded on new window without any inputs elements"
}

scenario "Displaying tooltip for marks on timeline", {
    given "HR is logged in the system"
    and "PR perspective page is loaded"
    when "HR hovers mark on timeline"
    then "Tooltip with PR date is displayed"
}

scenario "Export PR", {
    given "HR is logged in the system"
    and "PR perspective page is loaded"
    when "'Export' link is clicked"
    then "CSV file with all filled PR is downloaded"
}


