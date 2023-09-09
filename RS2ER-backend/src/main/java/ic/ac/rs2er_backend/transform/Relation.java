package ic.ac.rs2er_backend.transform;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

@Getter
@Setter
public class Relation implements Comparable<Relation> {
    private String name;
    private String type;
    private final TreeSet<String> primaryKeys;
    private final TreeSet<String> nonKeys;
    private int rows;

    private final TreeMap<String, RelationShip> foreignKeyWithLinks;
    private final TreeMap<String, RelationShip> primaryForeignKeys;
    private final TreeMap<String, ArrayList<RelationShip>> compoundForeignKeys;

    private TreeSet<RelationShip> basicEntityRelationShips;
    private TreeSet<RelationShip> weakEntityRelationShips;
    private TreeSet<RelationShip> oneManyRelationShips;
    private TreeSet<RelationShip> manyManyRelationShips;
    private TreeSet<RelationShip> reflexiveRelationShips;
    private boolean isJunctionRelation;

    public Relation(String name, String type) {
        this.name = name;
        this.type = type;
        this.primaryKeys = new TreeSet<>();
        this.foreignKeyWithLinks = new TreeMap<>();
        this.compoundForeignKeys = new TreeMap<>();
        this.primaryForeignKeys = new TreeMap<>();
        this.nonKeys = new TreeSet<>();
        this.rows = 0;
    }

    public void addPrimaryKey(String primaryKey) {
        this.primaryKeys.add(primaryKey);
    }

    public TreeMap<String, RelationShip> getForeignKeyWithLinks() {
        return foreignKeyWithLinks;
    }

    public void addForeignKeyWithLink(String foreignKey, RelationShip relationShip) {
        this.foreignKeyWithLinks.put(foreignKey, relationShip);
        if (primaryKeys.contains(foreignKey)) {
            this.primaryForeignKeys.put(foreignKey, relationShip);
        }
    }

    public void addCompoundForeignKeys(String parent, RelationShip relationShip) {
        if (this.compoundForeignKeys.get(parent) == null) {
            this.compoundForeignKeys.put(parent, new ArrayList<>());
        }
        this.compoundForeignKeys.get(parent).add(relationShip);
    }

    public void addNonKey(String nonKey) {
        this.nonKeys.add(nonKey);
    }

    public void addBasicEntityRelationship(RelationShip relationShip) {
        if (this.basicEntityRelationShips == null) {
            this.basicEntityRelationShips = new TreeSet<>();
        }
        this.basicEntityRelationShips.add(relationShip);
    }

    public void addWeakEntityRelationShips(RelationShip relationShip) {
        if (this.weakEntityRelationShips == null) {
            this.weakEntityRelationShips = new TreeSet<>();
        }
        this.weakEntityRelationShips.add(relationShip);
    }

    public void addOneManyRelationShip(RelationShip relationShip) {
        if (this.oneManyRelationShips == null) {
            this.oneManyRelationShips = new TreeSet<>();
        }
        this.oneManyRelationShips.add(relationShip);
    }

    public void addManyManyRelationShip(RelationShip relationShip) {
        if (this.manyManyRelationShips == null) {
            this.manyManyRelationShips = new TreeSet<>();
        }
        this.manyManyRelationShips.add(relationShip);
    }


    public void addReflexiveRelationShip(RelationShip relationShip) {
        if (this.reflexiveRelationShips == null) {
            this.reflexiveRelationShips = new TreeSet<>();
        }
        this.reflexiveRelationShips.add(relationShip);
    }


    public void printAllRelationships() {
        System.out.println("-------------------------------");
        System.out.println(name + "'s all relationships: ");
        if (this.basicEntityRelationShips != null) {
            System.out.println(name + "'s Basic Entity relationships: ");
            for (RelationShip relationShip : this.basicEntityRelationShips) {
                System.out.println(relationShip);
            }
        }
        if (this.weakEntityRelationShips != null) {
            System.out.println(name + "'s Weak Entity relationships: ");
            for (RelationShip relationShip : this.weakEntityRelationShips) {
                System.out.println(relationShip);
            }
        }
        if (this.oneManyRelationShips != null) {
            System.out.println(name + "'s One-Many relationships: ");
            for (RelationShip relationShip : this.oneManyRelationShips) {
                System.out.println(relationShip);
            }
        }
        if (this.manyManyRelationShips != null) {
            System.out.println(name + "'s Many-Many relationships: ");
            for (RelationShip relationShip : this.manyManyRelationShips) {
                System.out.println(relationShip);
            }
        }
        if (this.reflexiveRelationShips != null) {
            System.out.println(name + "'s reflexive relationships: ");
            for (RelationShip relationShip : this.reflexiveRelationShips) {
                System.out.println(relationShip);
            }
        }
    }

    public void printAllRelationships(String primaryKey) {
        System.out.println("-------------------------------");
        System.out.println(name + "'s all relationships with primary key '" + primaryKey + "': ");
        if (this.basicEntityRelationShips != null) {
            System.out.println(name + "'s Basic Entity relationships: ");
            for (RelationShip relationShip : this.basicEntityRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.weakEntityRelationShips != null) {
            System.out.println(name + "'s Weak Entity relationships with primary key '" + primaryKey + "': ");
            for (RelationShip relationShip : this.weakEntityRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.oneManyRelationShips != null) {
            System.out.println(name + "'s One-Many relationships with primary key '" + primaryKey + "': ");
            for (RelationShip relationShip : this.oneManyRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.manyManyRelationShips != null) {
            System.out.println(name + "'s Many-Many relationships with primary key '" + primaryKey + "': ");
            for (RelationShip relationShip : this.manyManyRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.reflexiveRelationShips != null) {
            System.out.println(name + "'s reflexive relationships with primary key '" + primaryKey + "': ");
            for (RelationShip relationShip : this.reflexiveRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        System.out.println();
    }

    public void getAllRelationships(String primaryKey) {
        TreeSet<RelationShip> relationships = new TreeSet<>();
        if (this.basicEntityRelationShips != null) {
            System.out.println(name + "'s Basic Entity relationships: ");
            for (RelationShip relationShip : this.basicEntityRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    relationships.add(relationShip);
                }
            }
        }
        if (this.weakEntityRelationShips != null) {
            for (RelationShip relationShip : this.weakEntityRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.oneManyRelationShips != null) {
            for (RelationShip relationShip : this.oneManyRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.manyManyRelationShips != null) {
            System.out.println(name + "'s Many-Many relationships with primary key '" + primaryKey + "': ");
            for (RelationShip relationShip : this.manyManyRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        if (this.reflexiveRelationShips != null) {
            System.out.println(name + "'s reflexive relationships with primary key '" + primaryKey + "': ");
            for (RelationShip relationShip : this.reflexiveRelationShips) {
                if (relationShip.getAttribute2().equals(primaryKey)) {
                    System.out.println(relationShip);
                }
            }
        }
        System.out.println();
    }

    @Override
    public String toString() {
        return "Relation{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", primaryKeys=" + primaryKeys +
                ", foreignKeyWithLinks=" + foreignKeyWithLinks +
                ", compoundKeys=" + compoundForeignKeys +
                ", nonKeys=" + nonKeys +
                ", rows=" + rows +
                ", isJunctionRelation=" + isJunctionRelation +
                '}';
    }

    @Override
    public int compareTo(Relation other) {
        return this.getName().compareTo(other.getName());
    }
}
