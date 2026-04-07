from sqlalchemy import Column, String, Boolean, DateTime, ForeignKey, Numeric, CheckConstraint, UniqueConstraint
from sqlalchemy.dialects.postgresql import UUID
from sqlalchemy.sql import func , text


from app.database.base import Base


class Profile(Base): 

    __tablename__ = "profile"

    user_id = Column(UUID(as_uuid = True), primary_key= True, server_default=text("gen_random_uuid()"))
    user_name = Column(String(255), nullable = False, unique = True)

class Favorite(Base): 
    __tablename__ = "favorite"

    id = Column(UUID(as_uuid = True), primary_key= True, server_default=text("gen_random_uuid()"))
    user_id = Column(UUID(as_uuid=True),ForeignKey("profile.user_id", ondelete = "CASCADE"), nullable= False)
    base_currency = Column(String(10), nullable= False)
    target_currency = Column(String(10), nullable= False)
    created_at = Column(DateTime(timezone= True), nullable= False, server_default= func.now())

    __table_args__ = (
        UniqueConstraint("user_id", "base_currency", "target_currency", name = "favorite_user_currency_unique"),
    )

class History(Base): 
    __tablename__ = "history"

    id = Column(UUID(as_uuid=True), primary_key=True, server_default=text("gen_random_uuid()"))
    user_id = Column(UUID(as_uuid=True),ForeignKey("profile.user_id", ondelete=
                                                   "CASCADE"), nullable= False)
    base_currency = Column(String(10), nullable= False)
    target_currency = Column(String(10),nullable= False)
    base_amount = Column(Numeric(12,2), nullable= False)
    converted_amount = Column(Numeric(12,2), nullable= False)
    rate = Column(Numeric(12,2), nullable= False)
    created_at = Column(DateTime(timezone= True),nullable= False, server_default=func.now())

class Alert(Base): 
    __tablename__ = "alert"

    id = Column(UUID(as_uuid= True), primary_key= True, server_default=text("gen_random_uuid()"))
    user_id = Column(UUID(as_uuid= True), ForeignKey("profile.user_id", ondelete= "CASCADE"), nullable = False)
    alert_target = Column(Numeric(12,2), nullable= False)
    direction = Column(String(10), nullable= False)
    last_checked_rate = Column(Numeric(12,2), nullable= True)
    last_checked_at = Column(DateTime(timezone= True), nullable= True, server_default= func.now())
    base_currency = Column(String(10), nullable= False)
    target_currency = Column(String(10), nullable= False)
    is_active = Column(Boolean, nullable= False, default = False)
    created_at = Column(DateTime(timezone=True), nullable= False, server_default= func.now())

    __table_args__ = (
        CheckConstraint("direction IN ('above', 'below')", name = "alert_direction_check"),
        UniqueConstraint("user_id", "base_currency", "target_currency", "direction", name = "alert_user_currency_direction_unique")
    )


