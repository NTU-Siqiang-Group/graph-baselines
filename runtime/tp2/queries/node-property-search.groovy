#META:SID=[0-10]

SID = System.env.get("SID").toInteger();

all_id_file_path = System.getenv("ALLIDPATH");
println("get all id from ${all_id_file_path}");

allIds = []
if (all_id_file_path.contains("janusgraph")) {
  allIds = get_long_ids_from_files(all_id_file_path);
} else {
  allIds = get_ids_from_files(all_id_file_path);
}

rand = new Random();

def execute_query(g,property_name,property_value,i,ORDER_j,DATABASE,DATASET,QUERY,ITERATION,OBJECT_ARRAY,SID){

	t = System.nanoTime();
	count = g.V.has(property_name, infer_type(property_value)).count();
	exec_time = System.nanoTime() - t;

        //DATABASE,DATASET,QUERY,SID,ITERATION,ORDER,TIME,OUTPUT,PARAMETER1(PROPERTY),PARAMETER2(VALUE)
	// result_row = [ DATABASE, DATASET, QUERY, String.valueOf(SID), ITERATION, String.valueOf(ORDER_j), String.valueOf(exec_time), count, String.valueOf(property_name), String.valueOf(property_value)];
	// println result_row.join(',');
	println("node property search used time " + exec_time + " ns");
}

// if (SID == NODE_ARRAY.size()) {
// 	order_j = 1;
// 	for (i in RAND_ARRAY) {
//         NODE_ID = infer_type(NODE_ARRAY[i])

//         execute_query(g,uid_field,NODE_ID,i,order_j,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID);
//         order_j++;
// 	}
// } else {
//     NODE_ID = infer_type(NODE_ARRAY[SID])

//     execute_query(g,uid_field,NODE_ID,SID,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID);
// }

vid = allIds[rand.nextInt() % allIds.size()];
execute_query(g,uid_field,vid,SID,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID);

//g.shutdown();
