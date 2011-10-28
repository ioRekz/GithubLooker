package controllers

import play._
import play.mvc._

import play.libs._


object Application extends Controller {
    
    import views.Application._
    
    def index() = {
        html.index()
    }
    
    def display() = {
        
        val repos = WS.url("https://github.com/api/v2/xml/repos/search/"+"epsi").get().getXml() \\ "repository"
      val projects = repos.map { repo =>
        ((repo \ "username").text, (repo \ "name").text)
      }
      
      html.index(projects)
    }
    
    def findRepo(query : String) = {
      val repos = WS.url("https://github.com/api/v2/xml/repos/search/"+query).get().getXml() \\ "repository"
      val projects = repos.map { repo =>
        ((repo \ "username").text, (repo \ "name").text)
      }
      html.index(projects)
    }
    
    /*def findCollabs(author : String, repo : String) = {
      val collabs = WS.url("https://github.com/api/v2/xml/repos/show/"+author+"/"+repo+"/collaborators").get().getXml() \\ "collaborator"
      
      val jl = new java.util.ArrayList [String] ()
      collabs.foreach { item => jl.add(item.text) }
      Json(jl)
    }*/
    
    def findCollabs(author : String, repo : String) = {
      val collabs = WS.url("https://github.com/api/v2/xml/repos/show/"+author+"/"+repo+"/contributors").get().getXml() \\ "contributor"
      
      <contributors>
      {
        collabs.map { cont =>
        
            <contributor>
              <login>{(cont\"login").text}</login>
              <contributions>{(cont\"contributions").text}</contributions>
            </contributor>
        }
      }</contributors>
    }
    
    def computeContributions(author : String, repo : String) = {
    
      val url = "https://github.com/api/v2/xml/commits/list/"+author+"/"+repo+"/master?page="
      //val counts = scala.collection.mutable.Map[String,Int]().withDefaultValue(0)
      val counts = scala.collection.mutable.Map[String,Int]()
      
      
      <project>
        <commits>
        {
          for(i <- 1 until 3 if WS.url(url+i).get().getStatus == 200) yield {
            val commits = WS.url(url+i).get().getXml() \\ "commit"
            commits.map { commit =>
              
              
              
              val login = (commit \ "committer" \ "login").text
              if(counts.contains(login)) counts(login) += 1
              else if (login!="") counts += login -> 1
              
              <commit>
                <id>{(commit \ "id").text}</id>
                <date>{(commit \ "committed-date").text}</date>
              </commit>
              
            }        
          }
        }</commits>
          
          
        <commiters>
        {
          counts.map { person =>
            <commiter>
              <login>{person._1}</login>
              <activity>{person._2}</activity>
            </commiter>
          }
        }
        </commiters>
          
      </project>
      
    }
    
    
}
