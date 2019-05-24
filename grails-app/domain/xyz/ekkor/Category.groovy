package xyz.ekkor

class Category {

    String code
    String labelCode
    String defaultLabel
    String url
    String iconCssNames

    boolean writable = true
    boolean enabled = true
    boolean isURL = false
    boolean adminOnly = false
    Integer level = 1
    Integer sortOrder = 0

    boolean useTag
    boolean useEvaluate
    boolean useNote
    boolean useOpinion
    boolean useSelectSolution = false
    boolean requireTag = false
    Boolean anonymity = false

    Boolean writeByExternalLink = false
    String externalLink
    Integer cate_role =5

    static belongsTo = [parent: Category]

    static hasMany = [children: Category]

    Date dateCreated
    Date lastUpdated

    static constraints = {
        code matches: /^[a-z_\-\d]+$/
        parent nullable: true
        url nullable: true
        useTag nullable: true
        useEvaluate nullable: true
        useNote nullable: true
        useOpinion nullable: true
        iconCssNames nullable: true
        anonymity nullable: true
        writeByExternalLink nullable: true
        externalLink nullable: true
        adminOnly nullable: true
    }

    /*static mapping = {
        id generator: "assigned", name: 'code', type: 'string'
        parent lazy: false
        children sort: 'sortOrder'
        sort 'sortOrder'
        cache true
    }*/

    static def getTopCategories() {
        Category.findAllByLevelAndEnabled(1, true)
    }
}
