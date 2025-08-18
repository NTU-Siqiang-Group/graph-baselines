#META:SID=[0-10]
SID = System.env.get("SID").toInteger(); 

PROPERTY_NAME= "test_specific_property";
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

def execute_query(g,ORDER_j,DATABASE,DATASET,QUERY,ITERATION,SID,PROP_NAME,PROP_VAL){

	t = System.nanoTime();
	count = g.E.has(PROP_NAME,PROP_VAL).count().next();
	exec_time = System.nanoTime() - t;

        //DATABASE,DATASET,QUERY,SID,ITERATION,ORDER,TIME,OUTPUT,PARAMETER1(PROPERTY),PARAMETER2(VALUE)
	// result_row = [ DATABASE, DATASET, QUERY, String.valueOf(SID), ITERATION, String.valueOf(ORDER_j), String.valueOf(exec_time), count, String.valueOf(PROP_NAME), String.valueOf(PROP_VAL)];
	// println result_row.join(',');
	println("edge property search used time " + exec_time + " ns");
}

// if (SID == EDGE_ARRAY.size()) { 
// 	order_j = 1;
// 	for (i in RAND_ARRAY) {
// 	    execute_query(g,order_j,DATABASE,DATASET,QUERY,ITERATION,SID,PROPERTY_NAME,(PROPERTY_VALUE+i));
// 	    order_j++;
// 	}
// } else {
// 	 execute_query(g,0,DATABASE,DATASET,QUERY,ITERATION,SID,PROPERTY_NAME,(PROPERTY_VALUE+(SID+1)));
// }

for (int i = 0; i < 10; i++) {
	v = g.V(allIds[rand.nextInt() % allIds.size()]);
	v.outE().next().property(PROPERTY_NAME,PROPERTY_VALUE);
}
if(!SKIP_COMMIT){
		try {
			g.tx().commit();
		} catch (MissingMethodException e) {
			System.err.println("Does not support g.tx().commit(). Ignoring.");
		}
}

execute_query(g,0,DATABASE,DATASET,QUERY,ITERATION,SID,PROPERTY_NAME,PROPERTY_VALUE);

//g.shutdown();
