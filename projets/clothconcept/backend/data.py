from typing import Generator, Sequence
from datetime import datetime


from sqlalchemy import create_engine, Column, Integer, String, Text, DateTime, Sequence
from sqlalchemy.orm import sessionmaker, Session, declarative_base
from sqlalchemy import Boolean

DATABASE_URL = "postgresql://postgres:postgres@localhost:5432/clotheConcept"

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class GeneratedDesignModel(Base):
    __tablename__ = "generated_designs"
    
    id = Column(
        Integer,
        Sequence('generated_designs_id_seq', start=1, increment=1),
        primary_key=True,
        index=True,
        autoincrement=True
    )
    gender = Column(String(20), nullable=False)
    category = Column(String(50), nullable=False)
    silhouette = Column(String(50), nullable=False)
    neckline = Column(String(50), nullable=True)
    sleeves = Column(String(50), nullable=True)
    waist = Column(String(50), nullable=True)
    fabric_type = Column(String(50), nullable=False)
    hex_color = Column(String(7), nullable=False)
    prompt = Column(Text, nullable=False)
    image_url_or_path = Column(Text, nullable=True)
    created_at = Column(DateTime, default=datetime.utcnow)

# Crée automatiquement les tables dans PostgreSQL si elles n'existent pas
Base.metadata.create_all(bind=engine)

# Dépendance FastAPI pour obtenir une session de base de données à chaque requête
def get_db() -> Generator[Session, None, None]:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


class Fabric(Base):
    __tablename__ = "fabrics"
    
    id = Column(String(50), primary_key=True)
    name = Column(String(100), nullable=False)
    type = Column(String(20), nullable=False)    
    texture_img_path = Column(String(500))    
    default_color = Column(String(7), nullable=False)
    allow_custom_color = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
