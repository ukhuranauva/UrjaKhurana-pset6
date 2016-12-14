Peer review done with Putri <br/>
-names (4):
*names are clear and consistent.

-headers (1):
*there are no headers

-comments(3):
*where comments exist, comments are good

-layout(3):
*layout is generally good, but some parts are very long and hard to keep track of (for example in EventAsyncTask and onContextItemSelected in MainActivity)

-formatting(4):
*formatting is good and consistent


-flow(3):
*flow is alright, but sometimes hard to keep track of; could be fixed with short high-level oneliners where methods are explained before actually having to look up the function/description

-idiom(3):
*Some functionality can perhaps be found in other existing classes (viewholders, recyclerviews)

*Maybe keeping track of the keys using keyList is not the neatest way and could somehow be avoided



-expressions(3):
*expressions are sometimes repeated and can be reduced: in EventAsyncTask lines may be omitted by not storing the intermediate jsonobjects but just getting them with toString() immediately

-decomposition(3):
*tasks may be decomposed further, for example the tasks in the cases of the switches in MainActivity


-modularization(4):
*modules are good and have limited communcation
