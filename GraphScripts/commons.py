#!/usr/bin/python
from unidecode import unidecode
import uuid, re, string

def get_hyphen_name(name):
    pattern = re.compile('[\W_]+');
    hyphen = unidecode ( name );
    hyphen = pattern.sub(" ", hyphen);
    hyphen = " ".join(hyphen.split()).strip();
    hyphen = hyphen.replace(" ","-").lower();
    return hyphen;

def get_uuid(url):
    uuid5 = uuid.uuid5(uuid.NAMESPACE_URL, url );
    str_uuid = str( uuid5.hex );
    return str_uuid;



