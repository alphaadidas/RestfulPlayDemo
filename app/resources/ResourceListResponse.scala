package resources

/**
 * @author gmatsu
 *
 *
 */
trait ResourceListResponse[ObjectType <: BaseResource] {

  def resourceName:String
  def resultCount:Int
  def results: List[ObjectType] = List.empty[ObjectType]

  //TODO: add support for "Token-Based Result Cursor Paging"
  def nextPageToken:Option[String]
  def hasMore:Boolean

}
