import play._
import play.test._

import org.scalatest._
import org.scalatest.junit._
import org.scalatest.matchers._
import scala.collection.mutable._

import play.libs._

class BasicTests extends UnitFlatSpec with ShouldMatchers {
    
    it should "run this dumb test" in {
        (1 + 1) should be (2)
    }
    
    it should "find repositories on github" in {
      val repos = WS.url("https://github.com/api/v2/xml/repos/search/epsi+chat").get().getXml() \\ "repository"
      repos.size should be (1)
      val tuples = repos.map { repo =>
      
        (repo \ "username").text
      }
      tuples(0) should be ("ioRekz")
      
    }
    
    it should "find collaborators" in {
      val repo = "mvallerie/slinky"
      val collabs = WS.url("https://github.com/api/v2/xml/repos/show/"+repo+"/collaborators").get().getXml() \\ "collaborator"
      val collaborators = collabs.map { 
        _.text
      }
      
      collaborators.size should be (3)
      collaborators(0) should be ("mvallerie")
    }
    
    it should "count contributions" in {
      val repo = "mvallerie/slinky"
      val url = "https://github.com/api/v2/xml/commits/list/"+repo+"/master?page="
      //val counts = scala.collection.mutable.Map[String,Int]().withDefaultValue(0)
      val counts = scala.collection.mutable.Map[String,Int]()
      for(i <- 1 until 3 if WS.url(url+i).get().getStatus == 200) {
        val commits = WS.url(url+i).get().getXml() \\ "commit"
        commits.foreach { commit =>
          
          val login = (commit \ "author" \ "login").text
          if(counts.contains(login)) counts(login) += 1
          else counts += login -> 1
          
        }        
      }      
      counts("ioRekz") should be (34)
    }

}