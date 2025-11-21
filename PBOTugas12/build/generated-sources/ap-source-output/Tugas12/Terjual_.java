package Tugas12;

import Tugas12.Sembako;
import java.sql.Date;
import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-11-21T19:53:39", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Terjual.class)
public class Terjual_ { 

    public static volatile SingularAttribute<Terjual, Integer> jumlah;
    public static volatile SingularAttribute<Terjual, String> idTerjual;
    public static volatile SingularAttribute<Terjual, Sembako> kodeBarang;
    public static volatile SingularAttribute<Terjual, Date> tanggal;

}