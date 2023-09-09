package ic.ac.rs2er_backend.transform;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class RelationShip implements Comparable<RelationShip> {
    private String relation1;
    private String attribute1;
    private String relation2;
    private String attribute2;

    //one-to-one or one-to-many or many-to-many
    private String type;
    //if the type is many-to-many, record the joined relation
    private String junctionRelation;

    public RelationShip(String relation1, String attribute1, String relation2, String attribute2, String type) {
        this.relation1 = relation1;
        this.attribute1 = attribute1;
        this.relation2 = relation2;
        this.attribute2 = attribute2;
        this.type = type;
    }

    @Override
    public String toString() {
        return "RelationShip{" +
                "relation1='" + relation1 + '\'' +
                ", attribute1='" + attribute1 + '\'' +
                ", relation2='" + relation2 + '\'' +
                ", attribute2='" + attribute2 + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelationShip that = (RelationShip) o;
        return Objects.equals(relation1, that.relation1) &&
                Objects.equals(attribute1, that.attribute1) &&
                Objects.equals(relation2, that.relation2) &&
                Objects.equals(attribute2, that.attribute2) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relation1, attribute1, relation2, attribute2, type);
    }

    @Override
    public int compareTo(RelationShip other) {
        return this.getRelation1().equals(other.getRelation1()) ?
                (this.getAttribute1().equals(other.getAttribute1()) ?
                        this.getRelation2().equals(other.getRelation2()) ?
                                this.getAttribute2().equals(other.getAttribute2()) ?
                                        0 : this.getAttribute2().compareTo(other.getAttribute2())
                                : this.getRelation2().compareTo(other.getRelation2())
                        : this.getAttribute1().compareTo(other.getAttribute1()))
                : this.getRelation1().compareTo(other.getRelation1());
    }
}
