package resources

/**
 * @author gmatsu
 *
 *
 */
trait ResourceListResponse[ObjectType <: BaseResource] {

  val resourceName = "Resource"
  val resultCount = 0
  val results: List[ObjectType] = List.empty[ObjectType]

//TODO: add support for "Token-Based Result Cursor Paging"
//  val nextPageToken = ""
//  val hasMore = true

}
