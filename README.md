# KedzieGiphy
Unlimited Scrolling Giphy Client

Implemented with Jetpack Compose *(where possible)*

_Note: Paging is implemented with legacy RecyclerView as the paging-compose library is buggy when used with maxSize and prepending.
However each item in the recycler is implemented with Compose
Also it does not support grids, only lists.  For this reason i chose to combine  compose with legacy views for the recyclerview.  
The navigation component is also legacy to support the recyclerview._

## Libraries
* Compose
* Kotlin Flows
* Jetpack Navigation v3
* Coil
* Hilt
* Retrofit
* Moshi
* RecyclerView alpha release which supports ComposeView in ViewHolders
