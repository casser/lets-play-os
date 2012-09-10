package os.letsplay.mongo;


public interface IQuery {
    Object getQuery();
	Object getFields();
	int getSkip();
	int getLimit();
}
