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

def execute_query(g,id,i,ORDER_j,DATABASE,DATASET,QUERY,ITERATION,OBJECT_ARRAY,SID,property_name){
    v = g.v(id);

    t = System.nanoTime();
    v.removeProperty(property_name);
    if (!SKIP_COMMIT) {
        try {
            g.commit();
        } catch (MissingMethodException e) {
            System.err.println("Does not support g.commit(). Ignoring.");
        }
    }
    exec_time = System.nanoTime() - t;
    size = v.map().size();

    //DATABASE,DATASET,QUERY,SID,ITERATION,ORDER,TIME,OUTPUT,PARAMETER1(NODE),PARAMETER2(PROPERTY)
    // result_row = [ DATABASE, DATASET, QUERY, String.valueOf(SID), ITERATION, String.valueOf(ORDER_j), String.valueOf(exec_time),size, String.valueOf(OBJECT_ARRAY[i]), String.valueOf(property_name)];
    // println result_row.join(',');
    println("delete node used time " + exec_time + " ns");
}

// if (SID == NODE_LID_ARRAY.size()) {
//     order_j = 1;
//     for (i in RAND_ARRAY) {
//         execute_query(g,NODE_LID_ARRAY[i],i,order_j,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,uid_field);
//         order_j++;
//     }
// } else {
//     execute_query(g,NODE_LID_ARRAY[SID],SID,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,uid_field);
// }
TEST_PROPERTY="new_property";
vid = allIds[rand.nextInt() % allIds.size()]
v = g.v(vid).setProperty(TEST_PROPERTY,TEST_PROPERTY);
if (!SKIP_COMMIT) {
    try {
        g.commit();
    } catch (MissingMethodException e) {
        System.err.println("Does not support g.commit(). Ignoring.");
    }
}
execute_query(g,vid,SID,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,uid_field);

//g.shutdown();
