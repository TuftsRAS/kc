DELIMITER /
create table BUDGET_SUB_AWARD_PERIOD_DETAIL (
	SUBAWARD_PERIOD_DETAIL_ID	DECIMAL(12,0)	NOT NULL,
	SUBAWARD_NUMBER				DECIMAL(12,0)	NOT NULL,
	BUDGET_ID					DECIMAL(12,0)	NOT NULL,
	BUDGET_PERIOD_NUMBER		DECIMAL(12,0)	NOT NULL,
	BUDGET_PERIOD				DECIMAL(3,0)		NOT NULL,
	DIRECT_COST					DECIMAL(12,2),
	INDIRECT_COST				DECIMAL(12,2),
	COST_SHARING_AMOUNT			DECIMAL(12,2),
	TOTAL_COST					DECIMAL(12,2),
	UPDATE_TIMESTAMP 			DATE 			NOT NULL, 
    UPDATE_USER 				VARCHAR(60) 	NOT NULL, 
    VER_NBR 					DECIMAL(8,0) 	DEFAULT 1 NOT NULL, 
    OBJ_ID 						VARCHAR(36) 	NOT NULL
)
/

alter table BUDGET_SUB_AWARD_PERIOD_DETAIL 
add constraint PK_BUDGET_SUB_AWARD_PER_DETAIL 
primary key (SUBAWARD_PERIOD_DETAIL_ID) 
/

DELIMITER ;
