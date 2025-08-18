#META:SID=[0-10]

SID = System.env.get("SID").toInteger();

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

def execute_query(g,id,i,ORDER_j,DATABASE,DATASET,QUERY,ITERATION,OBJECT_ARRAY,SID, PROP_NAME, PROP_VAL, SKIP_COMMIT){
    v = g.V(id).next();
    pp = v.property(PROP_NAME);

    t = System.nanoTime();

    v = v.property(PROP_NAME, PROP_VAL);

    if(!SKIP_COMMIT){
        try {
            g.tx().commit();
        } catch (MissingMethodException e) {
            System.err.println("Does not support g.tx().commit(). Ignoring.");
        }
    }
    exec_time = System.nanoTime() - t;
    // result_row = [ DATABASE, DATASET, QUERY, String.valueOf(SID), ITERATION, String.valueOf(ORDER_j), String.valueOf(exec_time),String.valueOf(v), String.valueOf(OBJECT_ARRAY[i]), String.valueOf(PROP_NAME), String.valueOf(PROP_VAL)];
    // println result_row.join(',');
    println("update node property used time " + exec_time + " ns");
}


PROPERTY_NAME= "test_common_property";
PROPERTY_VALUE = "test_value_new";


execute_query(g,allIds[rand.nextInt() % allIds.size()],SID,0,DATABASE,DATASET,QUERY,ITERATION,NODE_ARRAY,SID,PROPERTY_NAME,PROPERTY_VALUE,SKIP_COMMIT);

