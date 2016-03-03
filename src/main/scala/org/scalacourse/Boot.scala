package org.scalacourse

import com.synergygb.zordon.common.data.DataTransformContext
import com.synergygb.zordon.core.ServiceBoot
import com.synergygb.zordon.gen.models.{Task, Project}
import com.synergygb.zordon.gen.routes.ApplicationRoutesConsolidated
import spray.routing.Route

object Boot extends App with ServiceBoot with ApplicationRoutesConsolidated {

  implicit val context = Context

  import context.executionContext

  import Context.keyValueStore

  protected def apiResourceClass = getClass

  override def dataContext: DataTransformContext = context

  override def handlePostProjectProjectNameTask(projectName: String, taskInfo: Task)(): Route = {
    onSuccess(keyValueStore.read[Project](projectName)) { x =>
      val tasklist = x.map(_.tasks).getOrElse(Seq())
      val newTask = Task(taskInfo.taskName, taskInfo.user, taskInfo.complete)
      val newlist = tasklist ++ Seq(newTask)
      onSuccess(keyValueStore.write(projectName, newTask)) { y =>
        complete(newTask)
      }
    }
  }

  //get project percentage
  override def handleGetProjectsProjectName(projectName: String)(): Route = ?

  override def handlePostProjects(projectInfo: Project)(): Route = {
    val newProject = Project(projectInfo.finDate, projectInfo.projectName, projectInfo.initDate, projectInfo.leaderName, projectInfo.tasks)
    onSuccess(keyValueStore.write(newProject.projectName, newProject)) { x =>
      complete(newProject)
    }
  }

  override def handleDeleteProjectsProjectName(projectName: String)(): Route = {
    onSuccess(keyValueStore.read[Project](projectName)) { maybeProject =>
      onSuccess(keyValueStore.delete(projectName)) { deleteProject =>
        complete(maybeProject)
      }
    }
  }

  override def handlePutProjectProjectNameTaskTaskName(projectName: String, taskName: String)(): Route = ?

  override def handleDeleteProjectProjectNameTaskTaskName(projectName: String, taskName: String)(): Route = ?

  override def handleGetProjectProjectNameUserUserName(projectName: String, userName: String)(): Route = ?

  override def handlePutProjectsProjectName(projectName: String, projectInfo: Project)(): Route = {
    onSuccess(keyValueStore.read[Project](projectName)) { maybeProject =>
      onSuccess(keyValueStore.delete(projectName)) { deleteProject =>
        val updateProject = Project(projectInfo.finDate, projectInfo.projectName, projectInfo.initDate, projectInfo.leaderName, projectInfo.tasks)
        onSuccess(keyValueStore.write(projectName, updateProject)) { update =>
          complete(updateProject)
        }
      }
    }
  }
}
