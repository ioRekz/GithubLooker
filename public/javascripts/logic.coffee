jQuery -> 

cloud_list = []

findCollabs = (author,repo,htmlid) ->
  $.ajax(
    type: "GET",
    url: "/application/findCollabs",
    data: {author:author, repo:repo},
    dataType: "xml",
    success: (xml) ->
      $(xml).find('contributor').each ->
        login = $(this).find('login').text()
        $("#"+htmlid).append("<li>"+login+"</li>")
      
  )
  $.ajax(
    type: "GET",
    url: "/application/computeContributions",
    data: {author:author, repo:repo},
    dataType: "xml",
    success: (xml) ->
      $(xml).find('commiter').each ->
        login = $(this).find('login').text()
        contributions = $(this).find('activity').text()
        cloud_list.push {text:login, weight:contributions, url:"https://github.com/"+login}
      $("#contributors").empty()
      $("#contributors").jQCloud(cloud_list)
      
  )

prepareSlot = (i,item) ->
  values = item.split("*")
  htmlid = values[1].replace('.','')
  $('#stats').append $("<div>"+values.join(" - ")+"<ul style='list-style-type: none;' id="+htmlid+"></ul></div>")
  findCollabs(values[0],values[1],htmlid)

computeRepos = ->
  $.each(
    $('select').val()
    prepareSlot
  )

do computeRepos

$("select").change => 
  $("#stats").empty()
  $("#contributors").empty()
  cloud_list = []
  if $('select').val()
    do computeRepos
