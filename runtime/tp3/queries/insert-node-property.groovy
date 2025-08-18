#META:SID=[0-10]

SID = System.env.get("SID").toInteger();

PROPERTY_NAME= "test_new_specific_property";
PROPERTY_VALUE = "test_value_";

all_id_file_path = System.getenv("ALLIDPATH");
println("get all id from ${all_id_file_path}");

allIds = []
is_janus = false;
if (all_id_file_path.contains("janusgraph")) {
  allIds = f.get_long_ids_from_files(all_id_file_path);
  is_janus = true;
} else {
  allIds = f.get_ids_from_files(all_id_file_path);
}

rand = new Random();

def execute_query(g,id,i,ORDER_j,DATABASE,DATASET,QUERY,ITERATION,OBJECT_ARRAY,SID,PROP_NAME,PROP_VAL,SKIP_COMMIT){
    v = g.V(id);

    t = System.nanoTime();
    v.property(PROP_NAME,PROP_VAL).iterate();

    if(!SKIP_COMMIT){
      try {
         g.tx().commit();
      } catch (MissingMethodException e) {
         System.err.println("Does not support g.tx().commit(). Ignoring.");
      }
    }

    exec_time = System.nanoTime() - t;
    sizep =  g.V(id).valueMap().size();
    //DATABASE,DATASET,QUERY,SID,ITERATION,ORDER,TIME,OUTPUT,PARAMETER1(NODE)
	// result_row = [ DATABASE, DATASET, QUERY, String.valueOf(SID), ITERATION, String.valueOf(ORDER_j), String.valueOf(exec_time), sizep, String.valueOf(OBJECT_ARRAY[i]), PROP_NAME, PROP_VAL];
	// println result_row.join(',');
    println("insert-node-property used time: " + exec_time + " ns");
}

// if (SID == NODE_LID_ARRAY.size()) {
// 	order_j = 1;
// 	for (i in RAND_ARRAY) {
//         execute_query(g,NODE_LID_ARRAY[i],i,order_j,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,PROPERTY_NAME,(PROPERTY_VALUE+i), SKIP_COMMIT);
//         order_j++;
// 	}
// } else {
//     execute_query(g,NODE_LID_ARRAY[SID],SID,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,PROPERTY_NAME,(PROPERTY_VALUE+SID), SKIP_COMMIT);
// }
idx = rand.nextInt() % allIds.size();
execute_query(g,allIds[idx],idx,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,PROPERTY_NAME,(PROPERTY_VALUE+SID), SKIP_COMMIT);


//g.shutdown();
